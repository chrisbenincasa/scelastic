package com.chrisbenincasa.scelastic.builders

import com.chrisbenincasa.scelastic.params.{AggregationQueryParam, FromParam, QueryParam, SizeParam}
import com.chrisbenincasa.scelastic.queries.{AggregationQuery, Query}
import com.chrisbenincasa.scelastic.{ParameterizedBuilder, Params}

class SearchBuilder private (val params: Params) extends ParameterizedBuilder[SearchBuilder, Search] {
  def withQuery(query: Query): SearchBuilder = configured(QueryParam(query))
  def withFrom(from: Int): SearchBuilder = configured(FromParam(from))
  def withSize(size: Int): SearchBuilder = configured(SizeParam(size))
  def withAggregations(aggs: AggregationQuery): SearchBuilder = configured(AggregationQueryParam(aggs))

  def build: Search = {
    val QueryParam(query) = params[QueryParam]
    val size = params.optional[SizeParam].map(_.size)
    val from = params.optional[FromParam].map(_.from)

    Search(
      query = query,
      size = size,
      from = from,
      aggs = params.optional[AggregationQueryParam].map(_.aggregationQuery)
    )
  }

  override protected def copy1(params: Params): SearchBuilder = new SearchBuilder(params)
}

object SearchBuilder {
  def apply(): SearchBuilder = new SearchBuilder(Params.empty)
}

case class QueryClause(
  `match`: Option[Query]
)

case class Search(
  query: Query,
  size: Option[Int] = None,
  from: Option[Int] = None,
  aggs: Option[AggregationQuery] = None
)
