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
    case q"$pack.Property.apply(${ a: Ast }, ${ b: String })" => Property(a, b)
    case q"$pack.BinaryOperation.apply(${ a: Ast }, ${ b: BinaryOperator }, ${ c: Ast })" => BinaryOperation(a, b, c)
  }

  implicit val queryUnliftable: Unliftable[Query] = Unliftable[Query] {
    case q"$pack.Entity.apply(${a: String})" => Entity(a)
    case q"$pack.MatchAll" => MatchAll
    case q"$pack.MatchNone" => MatchNone
  }

  implicit val boolUnliftable: Unliftable[Query] = Unliftable[Query] {
    case q"$pack.Bool(${a: Ast}, ${b: Ast}, ${c: Ast})" => Bool(a, b, c)
    case q"$pack.BoolMust(${a: Ast}, ${b: Ast})" => BoolMust(a, b)
    case q"$pack.BoolFilter(${a: Ast}, ${b: Ast})" => BoolFilter(a, b)
  }

  implicit val binaryOperatorUnliftable: Unliftable[BinaryOperator] = Unliftable[BinaryOperator] {
    case q"$pack.EqualityOperator.`==`"       => EqualityOperator.`==`
    case q"$pack.EqualityOperator.`!=`"       => EqualityOperator.`!=`
    case q"$pack.RangeOperator.`>=`"          => RangeOperator.`>=`
    case q"$pack.RangeOperator.`>`"           => RangeOperator.`>`
    case q"$pack.RangeOperator.`<=`"          => RangeOperator.`<=`
    case q"$pack.RangeOperator.`<`"           => RangeOperator.`<`
  }

  implicit val identUnliftable: Unliftable[Ident] = Unliftable[Ident] {
    case q"$pack.Ident.apply(${ a: String })" => Ident(a)
  }

  implicit val valueUnliftable: Unliftable[Value] = Unliftable[Value] {
    case q"$pack.NullValue" => NullValue
    case q"$pack.Constant.apply(${Literal(c.universe.Constant(a))})" => Constant(a)
  }
}
