package com.chrisbenincasa.scelastic.builders

import com.chrisbenincasa.scelastic.params.{CompoundQueryParam, LeafQueryParam}
import com.chrisbenincasa.scelastic.queries._
import com.chrisbenincasa.scelastic.{ParameterizedBuilder, Params}

class QueryBuilder private[scelastic] (val params: Params) extends ParameterizedBuilder[QueryBuilder, Query] {
  def this() = this(Params.empty)

  // Leaf
  def withMatch(matchQuery: MatchQueryLike) = configured(LeafQueryParam(matchQuery)) // How do we handle bad configurations?
  def withTerm(termQuery: TermQueryLike) = configured(LeafQueryParam(termQuery))

  // Compound
  def withBool(boolCompoundQuery: BoolCompoundQuery) = configured(CompoundQueryParam(boolCompoundQuery))
  def withNested(nestedQuery: NestedCompoundQuery) = configured(CompoundQueryParam(nestedQuery))

//  def withAggregations()

  def build: Query =
    params.optional[LeafQueryParam].
      orElse(params.optional[CompoundQueryParam]).
      map(_.query).
      getOrElse(throw new IllegalStateException("No query configured"))

  override protected def copy1(params: Params): QueryBuilder = new QueryBuilder(params)
}

object QueryBuilder {
  def apply(): QueryBuilder = new QueryBuilder(Params.empty)
}
