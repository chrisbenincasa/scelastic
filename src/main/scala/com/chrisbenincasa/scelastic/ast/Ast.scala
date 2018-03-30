package com.chrisbenincasa.scelastic.ast

import com.chrisbenincasa.scelastic.params.QueryParam
import com.chrisbenincasa.scelastic.translate.AstStringTranslation

sealed trait Ast {
  override def toString: String = {
    import AstStringTranslation._
    import com.chrisbenincasa.scelastic.translate.TranslatorImplicits._
    this.translate.toString
  }
}

sealed trait Query extends Ast

case class Ident(name: String) extends Ast
case class Function(params: List[Ident], body: Ast) extends Ast
case class Property(ast: Ast, name: String) extends Ast
case class Block(statements: List[Ast]) extends Ast
case class Val(name: Ident, body: Ast) extends Ast

case class Entity(name: String) extends Query

sealed trait Value extends Ast
case object NullValue extends Value
case class Constant(v: Any) extends Value
case class Tuple(values: List[Ast]) extends Value

case class Dynamic(tree: Any) extends Ast
case class QuotedReference(tree: Any, ast: Ast) extends Ast

case object MatchAll extends Query
case object MatchNone extends Query

case class Bool(query: Ast, alias: Ast, clauses: List[Ast]) extends Query
case class BoolMust(query: Ast) extends Query
case class BoolFilter(query: Ast) extends Query

case class MatchQuery(query: Ast, alias: Ast, body: Ast, options: List[Ast] = Nil) extends Query
case class TermQuery(query: Ast, alias: Ast, body: Ast, options: List[Ast] = Nil) extends Query

sealed trait OptionParam extends Ast {
  def copy1(value: Ast): OptionParam
}
object OptionParam {
  import TermQueryOption._
  def unapply(arg: Ast): Option[(OptionParam, Ast)] = arg match {
    case a: boost => Some(a -> a.value)
    case a: operator => Some(a -> a.value)
    case _ => None
  }
}

sealed trait TermQueryOption extends OptionParam
object TermQueryOption {
  case class boost(value: Ast) extends TermQueryOption {
    override def copy1(value: Ast): boost = this.copy(value)
  }
  case class operator(value: Ast) extends OptionParam {
    override def copy1(value: Ast): operator = this.copy(value)
  }
}

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

object BooleanOperator {
  case object `&&` extends BinaryOperator
}
