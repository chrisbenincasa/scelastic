package com.chrisbenincasa.scelastic.ast.normalize

import com.chrisbenincasa.scelastic.ast._

object Optimize {
  def unapply(arg: Query): Option[Query] = {
    arg match {
      // Is this even possible or RIGHT!?!?
      case TermQuery(TermQuery(a, b, c, Nil), d, e, Nil) =>
        val er = BetaReduction(e, d -> b)
        Some(TermQuery(a, b, BinaryOperation(c, BooleanOperator.`&&`, er)))

      case _ => None
    }
  }
}
