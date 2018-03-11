package com.chrisbenincasa.scelastic.params

import com.chrisbenincasa.scelastic.queries.{AggregationQuery, CompoundQuery, MatchAllQuery, Query}
import com.chrisbenincasa.scelastic.{OptionalParam, Param}

case class QueryParam(query: Query)
object QueryParam {
  implicit val param: Param[QueryParam] = Param(QueryParam(MatchAllQuery))
}

case class AggregationQueryParam(aggregationQuery: AggregationQuery)
object AggregationQueryParam {
  implicit val param: Param[AggregationQueryParam] = Param.optional[AggregationQueryParam]
}

case class SizeParam(size: Int)
object SizeParam {
  implicit val param: Param[SizeParam] = Param.optional[SizeParam]
}

case class FromParam(from: Int)
object FromParam {
  implicit val param: Param[FromParam] with OptionalParam[FromParam] = Param.optional[FromParam]
}

trait ParamWithQueryField {
  val query: Query
}

case class LeafQueryParam(query: Query) extends ParamWithQueryField
object LeafQueryParam {
  implicit val param: Param[LeafQueryParam] = Param.optional[LeafQueryParam]
}

case class CompoundQueryParam(query: CompoundQuery[_]) extends ParamWithQueryField
object CompoundQueryParam {
  implicit val param: Param[CompoundQueryParam] = Param.optional[CompoundQueryParam]
}
