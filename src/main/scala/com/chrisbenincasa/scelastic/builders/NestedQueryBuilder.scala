package com.chrisbenincasa.scelastic.builders

import com.chrisbenincasa.scelastic.params.QueryParam
import com.chrisbenincasa.scelastic.queries.{NestedQuery, Query}
import com.chrisbenincasa.scelastic.{Param, ParameterizedBuilder, Params}

class NestedQueryBuilder private[scelastic] (
  path: String,
  val params: Params
) extends ParameterizedBuilder[NestedQueryBuilder, NestedQuery] {
  def withScoreMode(mode: String): NestedQueryBuilder = configured(ScoreModeParam(mode))

  def withQuery(query: Query): NestedQueryBuilder = configured(QueryParam(query))

  override def build: NestedQuery =
    NestedQuery(
      path = path,
      score_mode = params[ScoreModeParam].mode,
      query = params[QueryParam].query
    )

  override protected def copy1(params: Params): NestedQueryBuilder = new NestedQueryBuilder(path, params)
}

object NestedQueryBuilder {
  def apply(path: String): NestedQueryBuilder = new NestedQueryBuilder(path, Params.empty)
}

case class ScoreModeParam(mode: String)
object ScoreModeParam {
  implicit val param: Param[ScoreModeParam] = Param(ScoreModeParam("avg"))
}
