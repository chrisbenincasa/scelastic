package com.chrisbenincasa.scelastic.ast

trait AstWalk {
  val onNode: Ast => Unit

  def apply(ast: Ast): Unit = {
    ast match {
      case e: Query => apply(e)
      case e: Value => apply(e)
      case e: Operation => apply(e)
      case e: Ident => onNode(e)
      case e: Dynamic => onNode(e)
      case x @ Property(a, b) =>
        apply(a)
        onNode(x)
      case x @ Function(a, b) =>
        apply(b)
        onNode(x)
      case x @ Block(a) =>
        a.foreach(apply)
        onNode(x)
      case x @ Val(a, b) =>
        apply(b)
        onNode(x)
      case e: QuotedReference =>
        onNode(e)
      // Move ethis
      //      case OptionParam((x, a)) => x.copy1(apply(a))
      case x @ TermQueryOption.boost(a) =>
        apply(a)
        onNode(x)
      case x @ TermQueryOption.operator(a) =>
        apply(a)
        onNode(x)
    }
  }

  def apply(q: Query): Unit = {
    q match {
      case e: Entity =>
        onNode(e)
      case MatchAll =>
        onNode(MatchAll)
      case MatchNone =>
        onNode(MatchNone)
      case x @ Bool(a, b, c) =>
        apply(a)
        c.map(apply)
        onNode(x)
      case x @ BoolMust(a) =>
        apply(a)
        onNode(x)
      case x @ BoolMustNot(a) =>
        apply(a)
        onNode(x)
      case x @ BoolFilter(a) =>
        apply(a)
        onNode(x)
      case x @ MatchQuery(a, b, c, d) =>
        apply(a)
        apply(c)
        d.map(apply(_))
        onNode(x)
      case x @ TermQuery(a, b, c, d) =>
        apply(a)
        apply(c)
        d.map(apply(_))
        onNode(x)
      case x @ ExistsQuery(a, b, c) =>
        apply(a)
        apply(c)
        onNode(x)
    }
  }

  def apply(o: Operation): Unit = {
    o match {
      case x @ FunctionApply(function, values) =>
        apply(function)
        values.foreach(apply)
        onNode(x)
      case x @ BinaryOperation(a, b, c) =>
        apply(a)
        apply(c)
        onNode(x)
    }
  }

  def apply(e: Value): Unit =
    e match {
      case e: Constant   => onNode(e)
      case NullValue     => onNode(NullValue)
      case x @ Tuple(values) =>
        values.map(apply)
        onNode(x)
    }
}

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
