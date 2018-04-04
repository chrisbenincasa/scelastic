package com.chrisbenincasa.scelastic.ast

trait AstWalk {
  val onNode: (Ast, Int) => Unit

  def apply(ast: Ast): Unit = apply(ast, 0)

  def apply(ast: Ast, depth: Int): Unit = {
    ast match {
      case e: Query => apply(e, depth + 1)
      case e: Value => apply(e, depth + 1)
      case e: Operation => apply(e, depth + 1)
      case e: Ident => onNode(e, depth)
      case e: Dynamic => onNode(e, depth)
      case x @ Property(a, _) =>
        apply(a, depth + 1)
        onNode(x, depth)
      case x @ Function(_, b) =>
        apply(b, depth + 1)
        onNode(x, depth)
      case x @ Block(a) =>
        a.foreach(apply(_, depth + 1))
        onNode(x, depth)
      case x @ Val(_, b) =>
        apply(b, depth + 1)
        onNode(x, depth)
      case e: QuotedReference =>
        onNode(e, depth)
      // Move ethis
      //      case OptionParam((x, a)) => x.copy1(apply(a))
      case x @ TermQueryOption.boost(a) =>
        apply(a, depth + 1)
        onNode(x, depth)
      case x @ TermQueryOption.operator(a) =>
        apply(a, depth + 1)
        onNode(x, depth)
    }
  }

  def apply(q: Query, depth: Int): Unit = {
    q match {
      case e: Entity =>
        onNode(e, depth)
      case MatchAll =>
        onNode(MatchAll, depth)
      case MatchNone =>
        onNode(MatchNone, depth)
      case x @ Bool(a, b, c) =>
        apply(a, depth + 1)
        c.map(apply(_, depth + 1))
        onNode(x, depth)
      case x @ BoolMust(a) =>
        apply(a, depth + 1)
        onNode(x, depth)
      case x @ BoolMustNot(a) =>
        apply(a, depth + 1)
        onNode(x, depth)
      case x @ BoolFilter(a) =>
        apply(a, depth + 1)
        onNode(x, depth)
      case x @ MatchQuery(a, b, c, d) =>
        apply(a, depth + 1)
        apply(c, depth + 1)
        d.map(apply(_, depth + 1))
        onNode(x, depth)
      case x @ TermQuery(a, b, c, d) =>
        apply(a, depth + 1)
        apply(c, depth + 1)
        d.map(apply(_, depth + 1))
        onNode(x, depth)
      case x @ ExistsQuery(a, b, c) =>
        apply(a, depth + 1)
        apply(c, depth + 1)
        onNode(x, depth)
    }
  }

  def apply(o: Operation, depth: Int): Unit = {
    o match {
      case x @ FunctionApply(function, values) =>
        apply(function, depth + 1)
        values.foreach(apply(_, depth + 1))
        onNode(x, depth)
      case x @ BinaryOperation(a, b, c) =>
        apply(a, depth + 1)
        apply(c, depth + 1)
        onNode(x, depth)
    }
  }

  def apply(e: Value, depth: Int): Unit =
    e match {
      case e: Constant   => onNode(e, depth)
      case NullValue     => onNode(NullValue, depth)
      case x @ Tuple(values) =>
        values.map(apply(_, depth + 1))
        onNode(x, depth)
    }
}
