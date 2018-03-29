package com.chrisbenincasa.scelastic.ast

import scala.reflect.ClassTag

trait StatefulTransformer[T] {
  val state: T
  def nextState: StatefulTransformer[T] = this

  def apply(ast: Ast): (Ast, StatefulTransformer[T]) = {
    ast match {
      case e: Query => apply(e)
      case e: Value => apply(e)
      case e: Operation => apply(e)
      case e: Ident => e -> this
      case e: Dynamic => e -> this

      case Property(a, b) =>
        val (at, att) = apply(a)
        (Property(at, b), att)

      case Function(a, b) =>
        val (bt, btt) = apply(b)
        (Function(a, bt), btt)

      case Block(a) =>
        val (at, att) = apply(a)(_.apply)
        (Block(at), att)

      case Val(a, b) =>
        val (at, att) = apply(b)
        (Val(a, at), att)

      case QuotedReference(a, b) =>
        val (bt, btt) = apply(b)
        (QuotedReference(a, bt), btt)

      // Stick this into a different transformer
      case TermQueryOption.boost(a, b) =>
        val (at, att) = apply(a)
        val (bt, btt) = att.apply(b)
        TermQueryOption.boost(at, bt) -> btt
    }
  }

  def apply(q: Query): (Ast, StatefulTransformer[T]) = {
    q match {
      case e: Entity => e -> this
      case MatchAll => MatchAll -> this
      case MatchNone => MatchNone -> this
      case Bool(a, b, c) =>
        val (at, att) = apply(a)
        val (ct, ctt) = att.apply(c)
        Bool(at, b, ct) -> ctt
      case BoolMust(a, b) =>
        val (at, att) = apply(a)
        val (bt, btt) = att.apply(b)
        BoolMust(at, bt) -> btt
      case BoolFilter(a, b) =>
        val (at, att) = apply(a)
        val (bt, btt) = att.apply(b)
        BoolFilter(at, bt) -> btt
      case TermQuery(a, b, c) =>
        val (at, att) = apply(a)
        val (ct, ctt) = att.apply(c)
        TermQuery(at, b, ct) -> ctt
    }
  }

  def apply(e: Operation): (Operation, StatefulTransformer[T]) =
    e match {
      case BinaryOperation(a, b, c) =>
        val (at, att) = apply(a)
        val (ct, ctt) = att.apply(c)
        (BinaryOperation(at, b, ct), ctt)
      case FunctionApply(a, b) =>
        val (at, att) = apply(a)
        val (bt, btt) = att.apply(b)(_.apply)
        (FunctionApply(at, bt), btt)
    }

  def apply[U, R](list: List[U])(f: StatefulTransformer[T] => U => (R, StatefulTransformer[T])) =
    list.foldLeft((List.empty[R], this)) {
      case ((values, t), v) =>
        val (vt, vtt) = f(t)(v)
        (values :+ vt, vtt)
    }
}

class CollectAst[T](p: PartialFunction[Ast, T], val state: List[T])
  extends StatefulTransformer[List[T]] {

  override def apply(a: Ast) =
    a match {
      case d if p.isDefinedAt(d) => (d, new CollectAst(p, state :+ p(d)))
      case other                   => super.apply(other)
    }
}

object CollectAst {

  def byType[T: ClassTag](a: Ast) =
    apply[T](a) {
      case t: T => t
    }

  def apply[T](a: Ast)(p: PartialFunction[Ast, T]) =
    new CollectAst(p, List()).apply(a) match {
      case (_, transformer) =>
        transformer.state
    }
}

object IsDynamic {
  def apply(a: Ast) =
    CollectAst(a) { case d: Dynamic => d }.nonEmpty
}
