package com.chrisbenincasa.scelastic.dsl

import com.chrisbenincasa.scelastic.ast._
import scala.reflect.macros.whitebox

trait LiftableInstances {
  val c: whitebox.Context

  import c.universe.{ Ident => _, Constant => _, Function => _, If => _, Block => _, _ }

  private val pack = q"com.chrisbenincasa.scelastic.ast"

  implicit val astLiftable: Liftable[Ast] = Liftable[Ast] {
    case ast: Query => queryLiftable(ast)
    case ast: OptionParam => optionParamLiftable(ast)
    case ast: Ident => identLiftable(ast)
    case ast: Value => valueLiftable(ast)
    case Property(a, b) => q"$pack.Property($a, $b)"
    case BinaryOperation(a, b, c) => q"$pack.BinaryOperation($a, $b, $c)"
    case Dynamic(tree: Tree) if (tree.tpe <:< c.weakTypeOf[Dsl#Quoted[Any]]) => q"$tree.ast"
    case Dynamic(tree: Tree) => q"$pack.Constant($tree)"
    case QuotedReference(_: Tree, ast) => q"$ast"
  }

  implicit val queryLiftable: Liftable[Query] = Liftable[Query] {
    case Entity(a) => q"$pack.Entity($a)"
    case MatchAll => q"$pack.MatchAll"
    case MatchNone => q"$pack.MatchNone"
    case Bool(a, b, c) => q"$pack.Bool($a, $b, $c)"
    case BoolMust(a) => q"$pack.BoolMust($a)"
    case BoolMustNot(a) => q"$pack.BoolMustNot($a)"
    case BoolFilter(a) => q"$pack.BoolFilter($a)"
    case MatchQuery(a, b, c, d) => q"$pack.MatchQuery($a, $b, $c, $d)"
    case TermQuery(a, b, c, d) => q"$pack.TermQuery($a, $b, $c, $d)"
    case ExistsQuery(a, b, c) => q"$pack.ExistsQuery($a, $b, $c)"
  }

  implicit val binaryOperatorLiftable: Liftable[BinaryOperator] = Liftable[BinaryOperator] {
    case EqualityOperator.`==`       => q"$pack.EqualityOperator.`==`"
    case EqualityOperator.`!=`       => q"$pack.EqualityOperator.`!=`"
    case RangeOperator.`>=`          => q"$pack.RangeOperator.`>=`"
    case RangeOperator.`>`           => q"$pack.RangeOperator.`>`"
    case RangeOperator.`<=`          => q"$pack.RangeOperator.`<=`"
    case RangeOperator.`<`           => q"$pack.RangeOperator.`<`"
    case BooleanOperator.`&&`        => q"$pack.BooleanOperator.`&&`"
  }

  implicit val optionParamLiftable: Liftable[OptionParam] = Liftable[OptionParam] {
    case TermQueryOption.boost(value) => q"$pack.TermQueryOption.boost($value)"
    case TermQueryOption.operator(value) => q"$pack.TermQueryOption.operator($value)"
  }

  implicit val identLiftable: Liftable[Ident] = Liftable[Ident] {
    case Ident(a) => q"$pack.Ident($a)"
  }

  implicit val valueLiftable: Liftable[Value] = Liftable[Value] {
    case NullValue => q"$pack.NullValue"
    case Constant(a) => q"$pack.Constant(${Literal(c.universe.Constant(a))})"
    case Tuple(a) => q"$pack.Tuple($a)"
  }
}
