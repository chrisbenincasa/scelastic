package com.chrisbenincasa.scelastic.translate

import com.chrisbenincasa.scelastic.ast._
import TranslatorImplicits._

class AstStringTranslation {
  implicit val astToStringTranslator: Translator[Ast] = Translator[Ast] {
    case ast: Query                => ast.translate
    case ast: OptionParam          => ast.translate
    case ast: Function             => ast.translate
    case ast: Value                => ast.translate
    case ast: Operation            => ast.translate
    case ast: Ident                => ast.translate
    case ast: Block                => ast.translate
    case ast: Val                  => ast.translate
    case ast: Property             => ast.translate
    case ast: Dynamic              => ast.translate
    case ast: QuotedReference      => ast.ast.translate
  }

  implicit val queryTranslator: Translator[Query] = Translator[Query] {
    case Entity(name) => tokens"searchSchema(${s""""$name"""".translate})"
    case MatchAll => tokens"match_all"
    case MatchNone => tokens"match_none"
    case Bool(query, alias, body) => tokens"${query.translate}.bool(${alias.translate} => ${body.translate})"
    case BoolMust(query, body) => tokens"${query.translate}.must${body.translate}"
    case BoolFilter(query, body) => tokens"${query.translate}(${body.translate})"
    case TermQuery(query, alias, body) => tokens"${query.translate}.term((${alias.translate}) => ${body.translate})"
  }

  implicit val dynamicTranslator: Translator[Dynamic] = Translator[Dynamic] {
    case Dynamic(tree) => tokens"${tree.toString.translate}"
  }

  implicit val blockTranslator: Translator[Block] = Translator[Block] {
    case Block(statements) => tokens"{ ${statements.toStatement("; ")} }"
  }

  implicit val valTranslator: Translator[Val] = Translator[Val] {
    case Val(name, body) => tokens"val ${name.translate} = ${body.translate}"
  }

  implicit val functionTranslator: Translator[Function] = Translator[Function] {
    case Function(params, body) => tokens"(${params.translate}) => ${body.translate}"
  }

  implicit val propertyTranslator: Translator[Property] = Translator[Property] {
    case Property(ref, name) => tokens"${scopedTranslator(ref)}.${name.translate}"
  }

  implicit val operationTranslator: Translator[Operation] = Translator[Operation] {
    case BinaryOperation(a, op, b)                          => tokens"${scopedTranslator(a)} ${op.translate} ${scopedTranslator(b)}"
    case FunctionApply(function, values)                    => tokens"${scopedTranslator(function)}.apply(${values.translate})"
  }

  implicit val optionParamTranslator: Translator[OptionParam] = Translator[OptionParam] {
    case TermQueryOption.boost(query, value) => tokens"${query.translate}.boost(${value.translate})"
  }

  implicit def operatorTranslator[T <: Operator]: Translator[T] = Translator[T] {
    case o => tokens"${o.toString.translate}"
  }

  implicit val valueTranslator: Translator[Value] = Translator[Value] {
    case Constant(v: String) => tokens""""${v.translate}""""
    case Constant(())        => tokens"{}"
    case Constant(v)         => tokens"${v.toString.translate}"
    case NullValue           => tokens"null"
    case Tuple(values)       => tokens"(${values.translate})"
  }

  implicit val identTranslator: Translator[Ident] = Translator[Ident] {
    case e => tokens"${e.name.translate}"
  }

  private def scopedTranslator(ast: Ast) =
    ast match {
      case _: Function        => tokens"(${ast.translate})"
      case _: BinaryOperation => tokens"(${ast.translate})"
      case other              => ast.translate
    }
}

object AstStringTranslation extends AstStringTranslation
