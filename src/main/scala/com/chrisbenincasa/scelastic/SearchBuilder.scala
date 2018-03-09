package com.chrisbenincasa.scelastic

import com.chrisbenincasa.scelastic.params.{FromParam, QueryParam, SizeParam}
import com.chrisbenincasa.scelastic.queries.Query

class SearchBuilder private (val params: Params) extends ParameterizedBuilder[SearchBuilder] {
  def withQuery(query: ( Query )): SearchBuilder = configured(QueryParam(query))
  def withFrom(from: Int): SearchBuilder = configured(FromParam(from))
  def withSize(size: Int): SearchBuilder = configured(SizeParam(size))

  def build: Search = {
    val QueryParam(query) = params[QueryParam]
    val size = params.optional[SizeParam].map(_.size)
    val from = params.optional[FromParam].map(_.from)

    Search(
      query = query,
      size = size,
      from = from
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
  from: Option[Int] = None
)
