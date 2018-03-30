package com.chrisbenincasa.scelastic.ast.normalize

import com.chrisbenincasa.scelastic.ast._

object NormalizeNested {
  def unapply(arg: Query): Option[Query] = {
    arg match {
      case _: Entity => None
      case Bool(a, b, c) =>
        val normC = c.map(Normalize(_))
        normalize(a)(Bool(_, b, normC))
      case BoolMust(a) => normalize(a)(BoolMust)
      case BoolFilter(a) => normalize(a)(BoolFilter)
      case TermQuery(a, b, c, d) => normalize(a, c)(TermQuery(_, b, _, d))
      case _ => None
    }
  }

  private def normalize(a: Ast)(f: Ast => Query): Option[Query] = {
    Normalize(a) match {
      case `a` => None
      case a0 => Some(f(a0))
    }
  }

  private def normalize(a: Ast, b: Ast)(f: (Ast, Ast) => Query): Option[Query] = {
    (Normalize(a), Normalize(b)) match {
      case (`a`, `b`) => None
      case (a0, b0) => Some(f(a0, b0))
    }
  }
}
