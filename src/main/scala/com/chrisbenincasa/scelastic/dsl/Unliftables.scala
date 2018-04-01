package com.chrisbenincasa.scelastic.dsl

import com.chrisbenincasa.scelastic.ast._
import scala.reflect.macros.whitebox

trait Unliftables {
  val c: whitebox.Context

  import c.universe.{ Ident => _, Constant => _, Function => _, If => _, Block => _, _ }

  implicit val astUnliftable: Unliftable[Ast] = Unliftable[Ast] {
    case queryUnliftable(ast) => ast
    case boolUnliftable(ast) => ast
    case identUnliftable(ast) => ast
    case valueUnliftable(ast) => ast
    case optionParamUnliftable(ast) => ast
    case q"$pack.Property.apply(${ a: Ast }, ${ b: String })" => Property(a, b)
    case q"$pack.BinaryOperation.apply(${ a: Ast }, ${ b: BinaryOperator }, ${ c: Ast })" => BinaryOperation(a, b, c)
  }

  implicit val queryUnliftable: Unliftable[Query] = Unliftable[Query] {
    case q"$pack.Entity.apply(${a: String})" => Entity(a)
    case q"$pack.MatchAll" => MatchAll
    case q"$pack.MatchNone" => MatchNone
  }

  implicit def listUnliftable[T](implicit u: Unliftable[T]): Unliftable[List[T]] = Unliftable[List[T]] {
    case q"$pack.Nil"                         => Nil
    case q"$pack.List.apply[..$t](..$values)" => values.map(v => u.unapply(v).getOrElse(c.abort(c.enclosingPosition, s"Can't unlift $v")))
  }

  implicit val boolUnliftable: Unliftable[Query] = Unliftable[Query] {
    case q"$pack.Bool(${a: Ast}, ${b: Ast}, ${c: List[Ast]})" => Bool(a, b, c)
    case q"$pack.BoolMust(${a: Ast})" => BoolMust(a)
    case q"$pack.BoolMustNot(${a: Ast})" => BoolMustNot(a)
    case q"$pack.BoolFilter(${a: Ast})" => BoolFilter(a)
    case q"$pack.MatchQuery(${a: Ast}, ${b: Ast}, ${c: Ast})" => MatchQuery(a, b, c)
    case q"$pack.MatchQuery(${a: Ast}, ${b: Ast}, ${c: Ast}, ${d: List[Ast]})" => MatchQuery(a, b, c, d)
    case q"$pack.TermQuery(${a: Ast}, ${b: Ast}, ${c: Ast})" => TermQuery(a, b, c)
    case q"$pack.TermQuery(${a: Ast}, ${b: Ast}, ${c: Ast}, ${d: List[Ast]})" => TermQuery(a, b, c, d)
    case q"$pack.ExistsQuery(${a: Ast}, ${b: Ast}, ${c: Ast})" => ExistsQuery(a, b, c)
  }

  implicit val binaryOperatorUnliftable: Unliftable[BinaryOperator] = Unliftable[BinaryOperator] {
    case q"$pack.EqualityOperator.`==`"       => EqualityOperator.`==`
    case q"$pack.EqualityOperator.`!=`"       => EqualityOperator.`!=`
    case q"$pack.RangeOperator.`>=`"          => RangeOperator.`>=`
    case q"$pack.RangeOperator.`>`"           => RangeOperator.`>`
    case q"$pack.RangeOperator.`<=`"          => RangeOperator.`<=`
    case q"$pack.RangeOperator.`<`"           => RangeOperator.`<`
    case q"$pack.BooleanOperator.`&&`"        => BooleanOperator.`&&`
  }

  implicit val optionParamUnliftable: Unliftable[OptionParam] = Unliftable[OptionParam] {
    case q"$pack.TermQueryOption.boost.apply(${value: Ast})" => TermQueryOption.boost(value)
  }

  implicit val identUnliftable: Unliftable[Ident] = Unliftable[Ident] {
    case q"$pack.Ident.apply(${ a: String })" => Ident(a)
  }

  implicit val valueUnliftable: Unliftable[Value] = Unliftable[Value] {
    case q"$pack.NullValue" => NullValue
    case q"$pack.Constant.apply(${Literal(c.universe.Constant(a))})" => Constant(a)
    case q"$pack.Tuple.apply(${a : List[Ast]})" => Tuple(a)
  }
}
