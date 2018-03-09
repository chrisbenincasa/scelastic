package com.chrisbenincasa.scelastic

import com.chrisbenincasa.scelastic.params.{CompoundQueryParam, LeafQueryParam}
import com.chrisbenincasa.scelastic.queries._

class QueryBuilder private[scelastic] (val params: Params) extends ParameterizedBuilder[QueryBuilder] {
  def this() = this(Params.empty)

  // Leaf
  def withMatch(matchQuery: MatchQueryLike) = configured(LeafQueryParam(matchQuery)) // How do we handle bad configurations?
  def withTerm(termQuery: TermQueryLike) = configured(LeafQueryParam(termQuery))

  // Compound
  def withBool(boolCompoundQuery: BoolCompoundQuery) = configured(CompoundQueryParam(boolCompoundQuery))
  def withNested(nestedQuery: NestedCompoundQuery) = configured(CompoundQueryParam(nestedQuery))

  def build: Query =
    params.optional[LeafQueryParam].
      orElse(params.optional[CompoundQueryParam]).
      map(_.query).
      getOrElse(throw new IllegalStateException("No query configured"))

  override protected def copy1(params: Params): QueryBuilder = new QueryBuilder(params)
}

class NestedQueryBuilder private (params: Params) extends QueryBuilder(params) {

}

object QueryBuilder {
  def apply(): QueryBuilder = new QueryBuilder(Params.empty)
}
