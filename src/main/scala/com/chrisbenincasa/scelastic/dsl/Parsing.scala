package com.chrisbenincasa.scelastic.dsl

import com.chrisbenincasa.scelastic.ast._
import scala.reflect.macros.TypecheckException

trait Parsing {
  this: QuoteMacro =>

  import c.universe.{ Ident => _, Constant => _, Function => _, If => _, Block => _, _ }
  import scala.reflect.ClassTag

  case class Parser[T](p: PartialFunction[Tree, T])(implicit ct: ClassTag[T]) {
    def apply(tree: Tree) = {
      unapply(tree).getOrElse {
        c.abort(c.enclosingPosition, s"Tree '$tree' can't be parsed to '${ct.runtimeClass.getSimpleName}'\nRaw: ${showRaw(tree)}")
      }
    }

    def unapply(arg: Tree): Option[T] = {
      p.lift(arg)
    }
  }

  val astParser: Parser[Ast] = Parser[Ast] {
    case q"${t: Tree}: ${_ : Tree}" => astParser(t)
    case `queryParser`(v) => v
    case `operationParser`(v) => v
    case `quotedAstParser`(v) => v
    case `boolQueryParser`(v) => v
    case `termOptionParser`(v) => v
    case `identParser`(v) => v
    case `equalityOperationParser`(v) => v
    case `blockParser`(v) => v
    case `valueParser`(v) => v
    case `propertyParser`(v) => v
  }

  val blockParser: Parser[Block] = Parser[Block] {
    case q"{..$exprs}" if exprs.size > 1 => Block(exprs.map(astParser(_)).toList)
  }

  val queryParser: Parser[Ast] = Parser[Ast] {
    case q"$pack.search[$t]" =>
      Entity("???")

    case q"$pack.searchSchema[$t](${ name: String })" =>
      Entity(name)

    case q"$source.bool[$t]({($alias) => $body})" if source.tpe <:< typeTag[Dsl#Search[Any]].tpe =>
      Bool(astParser(source), identParser(alias), astParser(body))
  }

  val boolQueryParser: Parser[Ast] = Parser[Ast] {
    case q"$source.must.match_all" if source.tpe <:< typeTag[Dsl#BoolSearch[Any]].tpe =>
      BoolMust(identParser(source), MatchAll)

    case q"$source.must.match_none" if source.tpe <:< typeTag[Dsl#BoolSearch[Any]].tpe =>
      BoolMust(identParser(source), MatchNone)

    case q"$source.must.`match`({($alias) => $body})" if source.tpe <:< typeTag[Dsl#BoolSearch[Any]].tpe =>
      BoolMust(identParser(source), astParser(body))

    case q"$source.term({($alias) => $body})" if is[Dsl#FilterContext[Any]](source) =>
      TermQuery(astParser(source), identParser(alias), astParser(body))
  }

  val termOptionParser: Parser[Ast] = Parser[Ast] {
    case q"$source.boost($value)" if is[Dsl#TermNode[Any]](source) =>
      TermQueryOption.boost(astParser(source), astParser(value))
  }

  val operationParser: Parser[Operation] = Parser[Operation] {
    case `equalityOperationParser`(value) => value
  }

  private def rejectOptions(a: Tree, b: Tree): Unit = {
    if ((isNull(a) && isOption(a)) || (!isNull(b) && isOption(b))) {
      c.abort(a.pos, "Can't compare `Option` values since databases have different behavior for null comparison. Use `Option` methods like `forall` and `exists` instead.")
    }
  }

  val equalityOperationParser: Parser[Operation] = Parser[Operation] {
    case q"${a: Tree}.==(${b: Tree})" =>
      checkTypes(a, b)
      rejectOptions(a, b)
      BinaryOperation(astParser(a), EqualityOperator.`==`, astParser(b))
    case q"${a: Tree}.equals(${b: Tree})" =>
      checkTypes(a, b)
      rejectOptions(a, b)
      BinaryOperation(astParser(a), EqualityOperator.`==`, astParser(b))
    case q"${a: Tree}.!=(${b: Tree})" =>
      checkTypes(a, b)
      rejectOptions(a, b)
      BinaryOperation(astParser(a), EqualityOperator.`!=`, astParser(b))
  }

  val quotedAstParser: Parser[Ast] = Parser[Ast] {
    case q"${_: Tree}.unquote[${_: Tree}](${quoted: Tree})" =>
      astParser(quoted)

    case t if t.tpe <:< c.weakTypeOf[Dsl#Quoted[Any]] =>
      unquote[Ast](t) match {
        case Some(ast) if !IsDynamic(ast) =>
          t match {
            case _: c.universe.Block => ast // expand quote(quote(body)) locally
            case t0 =>
              Rebind(c)(t0, ast, astParser(_)) match {
                case Some(reboundAst) => reboundAst
                case None      => QuotedReference(t0, ast)
              }
          }
        case _ => Dynamic(t)
      }
  }

  val identParser: Parser[Ident] = Parser[Ident] {
    case t: ValDef                        => identClean(Ident(t.name.decodedName.toString))
    case c.universe.Ident(TermName(name)) => identClean(Ident(name))
    case q"${_: Tree}.this.$i"                  => identClean(Ident(i.decodedName.toString))
    case c.universe.Bind(TermName(name), c.universe.Ident(termNames.WILDCARD)) => identClean(Ident(name))
  }
  private def identClean(x: Ident): Ident = x.copy(name = x.name.replace("$", ""))
  private def ident(x: TermName): Ident = identClean(Ident(x.decodedName.toString))

  val valueParser: Parser[Ast] = Parser[Ast] {
    case q"null" => NullValue
    case Literal(c.universe.Constant(v)) => Constant(v)
  }

  val propertyParser: Parser[Ast] = Parser[Ast] {
    case q"$e.get" if isOptionT[Any](e) =>
      c.abort(c.enclosingPosition, "Option.get is not supported since it's an unsafe operation. Use `forall` or `exists` instead.")
    case q"$e.$property" => Property(astParser(e), property.decodedName.toString)
  }

  private def is[T](tree: Tree)(implicit t: TypeTag[T]) = tree.tpe <:< t.tpe
  private def isNull(tree: Tree) = is[Null](tree)
  private def isOption(tree: Tree) = is[Option[_]](tree)
  private def isOptionT[T](tree: Tree)(implicit t: TypeTag[T]) = is[Option[T]](tree)

  private def checkTypes(lhs: Tree, rhs: Tree): Unit = {
    def unquoted(tree: Tree) = if (!is[Dsl#Quoted[Any]](tree)) tree else q"unquote($tree)"

    val t = TypeName(c.freshName("T"))

    try c.typecheck(
      q"""
        def apply[$t](lhs: $t)(rhs: $t) = ()
        apply(${unquoted(lhs)})($rhs)
      """,
      c.TYPEmode
    ) catch {
      case t: TypecheckException => c.error(lhs.pos, t.msg)
    }
    ()
  }
}
