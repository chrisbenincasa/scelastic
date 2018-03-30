package com.chrisbenincasa.scelastic.ast.normalize

import com.chrisbenincasa.scelastic.ast.{Ast, BetaReduction, Query, Traversal}
import scala.annotation.tailrec

object Normalize extends Traversal {
  override def apply(ast: Ast): Ast = {
    super.apply(BetaReduction(ast))
  }

  override def apply(q: Query): Ast = {
    normalize(q)
  }

  @tailrec
  private def normalize(q: Query): Query = {
    q match {
      case NormalizeNested(q2) => normalize(q2)
//      case FlattenQueryOptions(q2) => normalize(q2)
      case Optimize(q2) => normalize(q2)
      case x => x
    }
  }
}
