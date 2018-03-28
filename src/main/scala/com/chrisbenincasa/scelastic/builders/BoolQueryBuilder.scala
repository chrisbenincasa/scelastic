package com.chrisbenincasa.scelastic.builders

import com.chrisbenincasa.scelastic.params.CompoundQueryParam
import com.chrisbenincasa.scelastic.queries.{BoolCompoundQuery, Query}
import com.chrisbenincasa.scelastic.{ParameterizedBuilder, Params}

class BoolQueryBuilder private (val params: Params) extends ParameterizedBuilder[BoolQueryBuilder, BoolCompoundQuery] {
  def withMust(qs: Query*): BoolQueryBuilder = configuredCopy(_.copy(must = Some(qs.seq)))
  def addMust(qs: Query*): BoolQueryBuilder = configuredCopy(curr => curr.copy(must = Some(curr.must.getOrElse(Seq.empty) ++ qs.seq)))

  def withFilter(qs: Query*): BoolQueryBuilder = configuredCopy(_.copy(filter = Some(qs.seq)))
  def addFilter(qs: Query*): BoolQueryBuilder = configuredCopy(curr => curr.copy(filter = Some(curr.filter.getOrElse(Seq.empty) ++ qs.seq)))

  def withMustNot(qs: Query*): BoolQueryBuilder = configuredCopy(_.copy(must_not = Some(qs.seq)))
  def addMustNot(qs: Query*): BoolQueryBuilder = configuredCopy(curr => curr.copy(must_not = Some(curr.must_not.getOrElse(Seq.empty) ++ qs.seq)))

  def withShould(qs: Query*): BoolQueryBuilder = configuredCopy(_.copy(should = Some(qs.seq)))
  def addShould(qs: Query*): BoolQueryBuilder = configuredCopy(curr => curr.copy(should = Some(curr.should.getOrElse(Seq.empty) ++ qs.seq)))

  def withBoost(double: Double): BoolQueryBuilder = configuredCopy(_.copy(boost = Some(double)))

  def build: BoolCompoundQuery = params[CompoundQueryParam].query.asInstanceOf[BoolCompoundQuery]

  private def configuredCopy(f: BoolQueryStruct => BoolQueryStruct): BoolQueryBuilder = {
    val struct = params.optional[CompoundQueryParam].map(_.query) match {
      case Some(BoolCompoundQuery(s)) => s
      case None => BoolQueryStruct()
      case _ => throw new IllegalStateException("")
    }

    configured(CompoundQueryParam(BoolCompoundQuery(f(struct))))
  }

  override protected def copy1(params: Params): BoolQueryBuilder = {
    new BoolQueryBuilder(params)
  }
}

object BoolQueryBuilder {
  def apply(): BoolQueryBuilder = new BoolQueryBuilder(Params.empty)
}

case class BoolQueryStruct(
  must: Option[Seq[Query]] = None,
  filter: Option[Seq[Query]] = None,
  must_not: Option[Seq[Query]] = None,
  should: Option[Seq[Query]] = None,
  minimum_should_match: Option[Int] = None,
  boost: Option[Double] = None
)
