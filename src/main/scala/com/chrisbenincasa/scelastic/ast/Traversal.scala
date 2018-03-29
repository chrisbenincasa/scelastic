package com.chrisbenincasa.scelastic.ast

trait Traversal {

  def apply(ast: Ast): Ast = {
    ast match {
      case e: Query => apply(e)
      case e: Value => apply(e)
      case e: Operation => apply(e)
      case e: Ident => e
      case e: Dynamic => e
      case Property(a, b) => Property(apply(a), b)
      case Function(a, b) => Function(a, apply(b))
      case Block(a) => Block(a.map(apply))
      case Val(a, b) => Val(a, apply(b))
      case e: QuotedReference => e
        // Move ethis
      case TermQueryOption.boost(a, b) => TermQueryOption.boost(apply(a), apply(b))
    }
  }

  def apply(q: Query): Ast = {
    q match {
      case e: Entity => e
      case MatchAll => MatchAll
      case MatchNone => MatchNone
      case Bool(a, b, c) => Bool(apply(a), b, apply(c))
      case BoolMust(a, b) => BoolMust(apply(a), apply(b))
      case BoolFilter(a, b) => BoolFilter(apply(a), apply(b))
      case TermQuery(a, b, c) => TermQuery(apply(a), b, apply(c))
    }
  }

  def apply(o: Operation): Ast = {
    o match {
      case FunctionApply(function, values) => FunctionApply(apply(function), values.map(apply))
      case BinaryOperation(a, b, c) => BinaryOperation(apply(a), b, apply(c))
    }
  }

  def apply(e: Value): Value =
    e match {
      case e: Constant   => e
      case NullValue     => NullValue
      case Tuple(values) => Tuple(values.map(apply))
    }
}
