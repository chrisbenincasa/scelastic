package com.chrisbenincasa.scelastic.ast

sealed trait Ast

sealed trait Query extends Ast

case class Ident(name: String) extends Ast
case class Function(params: List[Ident], body: Ast) extends Ast
case class Property(ast: Ast, name: String) extends Ast
case class Block(statements: List[Ast]) extends Ast

case class Entity(name: String) extends Query

sealed trait Value extends Ast
case object NullValue extends Value
case class Constant(v: Any) extends Value

case class Dynamic(tree: Any) extends Ast
case class QuotedReference(tree: Any, ast: Ast) extends Ast

case object MatchAll extends Query
case object MatchNone extends Query

case class Bool(query: Ast, alias: Ast, body: Ast) extends Query
case class BoolMust(query: Ast, body: Ast) extends Query
case class BoolFilter(query: Ast, body: Ast) extends Query

sealed trait Operation extends Ast
case class BinaryOperation(a: Ast, operator: BinaryOperator, b: Ast) extends Operation
case class FunctionApply(function: Ast, values: List[Ast]) extends Operation

sealed trait Operator
sealed trait BinaryOperator extends Operator
object EqualityOperator {
  case object `==` extends BinaryOperator
  case object `!=` extends BinaryOperator
}

object RangeOperator {
  case object `>=` extends BinaryOperator
  case object `>` extends BinaryOperator
  case object `<=` extends BinaryOperator
  case object `<` extends BinaryOperator
//  case object gte extends BinaryOperator
//  case object gt extends BinaryOperator
//  case object lt extends BinaryOperator
//  case object lte extends BinaryOperator
}
