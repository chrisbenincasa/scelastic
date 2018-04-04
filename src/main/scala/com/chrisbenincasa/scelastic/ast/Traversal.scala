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
//      case OptionParam((x, a)) => x.copy1(apply(a))
      case TermQueryOption.boost(a) => TermQueryOption.boost(apply(a))
      case TermQueryOption.operator(a) => TermQueryOption.operator(apply(a))
    }
  }

  def apply(q: Query): Ast = {
    q match {
      case e: Entity => e
      case MatchAll => MatchAll
      case MatchNone => MatchNone
      case Bool(a, b, c) => Bool(apply(a), b, c.map(apply))
      case BoolMust(a) => BoolMust(apply(a))
      case BoolMustNot(a) => BoolMustNot(apply(a))
      case BoolFilter(a) => BoolFilter(apply(a))
      case MatchQuery(a, b, c, d) => MatchQuery(apply(a), b, apply(c), d.map(apply))
      case TermQuery(a, b, c, d) => TermQuery(apply(a), b, apply(c), d.map(apply))
      case ExistsQuery(a, b, c) => ExistsQuery(apply(a), b, apply(c))
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
