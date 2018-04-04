package com.chrisbenincasa.scelastic.builders

import com.chrisbenincasa.scelastic.params.CompoundQueryParam
import com.chrisbenincasa.scelastic.queries.{BoolCompoundQuery, Query}
import com.chrisbenincasa.scelastic.{ParameterizedBuilder, Params}

class BoolQueryBuilder private (val params: Params) extends ParameterizedBuilder[BoolQueryBuilder, BoolCompoundQuery] {
  def withMust(qs: Query*): BoolQueryBuilder = {
    if (qs.isEmpty) this
    else configuredCopy(_.copy(must = Some(qs.seq)))
  }
  def addMust(qs: Query*): BoolQueryBuilder = configuredCopy(curr => curr.copy(must = Some(curr.must.getOrElse(Seq.empty) ++ qs.seq)))

  def withFilter(qs: Query*): BoolQueryBuilder = {
    if (qs.isEmpty) this
    else configuredCopy(_.copy(filter = Some(qs.seq)))
  }
  def addFilter(qs: Query*): BoolQueryBuilder = configuredCopy(curr => curr.copy(filter = Some(curr.filter.getOrElse(Seq.empty) ++ qs.seq)))

  def withMustNot(qs: Query*): BoolQueryBuilder = {
    if (qs.isEmpty) this
    else configuredCopy(_.copy(must_not = Some(qs.seq)))
  }
  def addMustNot(qs: Query*): BoolQueryBuilder = configuredCopy(curr => curr.copy(must_not = Some(curr.must_not.getOrElse(Seq.empty) ++ qs.seq)))

  def withShould(qs: Query*): BoolQueryBuilder = {
    if (qs.isEmpty) this
    else configuredCopy(_.copy(should = Some(qs.seq)))
  }
  def addShould(qs: Query*): BoolQueryBuilder = configuredCopy(curr => curr.copy(should = Some(curr.should.getOrElse(Seq.empty) ++ qs.seq)))

  def withBoost(double: Double): BoolQueryBuilder = configuredCopy(_.copy(boost = Some(double)))

  def build: BoolCompoundQuery = params[CompoundQueryParam].query.asInstanceOf[BoolCompoundQuery]

  def merge(other: BoolQueryBuilder): BoolQueryBuilder = {
    val BoolQueryExtractor(otherQuery) = other
    val BoolQueryExtractor(thisQuery) = this

    val newMust = (thisQuery.must.toList ++ otherQuery.must.toList).flatten
    val newFilter = (thisQuery.filter.toList ++ otherQuery.filter.toList).flatten
    val q = BoolQueryStruct(
      must = if ((thisQuery.must.isEmpty && otherQuery.must.isEmpty) || newMust.isEmpty) None else Some(newMust),
      filter = if ((thisQuery.filter.isEmpty && otherQuery.filter.isEmpty) || newFilter.isEmpty) None else Some(newFilter)
    )

    BoolQueryBuilder(BoolCompoundQuery(q))
  }

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

  private object BoolQueryExtractor {
    def unapply(arg: BoolQueryBuilder): Option[BoolQueryStruct] = {
      arg.params.optional[CompoundQueryParam].map(_.query) match {
        case Some(BoolCompoundQuery(s)) => Some(s)
        case None => Some(BoolQueryStruct())
        case _ => None
      }
    }
  }
}

object BoolQueryBuilder {
  def apply(): BoolQueryBuilder = new BoolQueryBuilder(Params.empty)
  def apply(q: BoolCompoundQuery): BoolQueryBuilder = {
    q.boolQueryStruct match {
      case BoolQueryStruct(must, filter, mustNot, should, minMatch, boost) =>
        val builder = BoolQueryBuilder().
          withMust(must.toList.flatten: _*).
          withMustNot(mustNot.toList.flatten: _*).
          withFilter(filter.toList.flatten: _*).
          withShould(should.toList.flatten: _*)

        boost match {
          case Some(b) => builder.withBoost(b)
          case None => builder
        }
    }
  }
}

case class BoolQueryStruct(
  must: Option[Seq[Query]] = None,
  filter: Option[Seq[Query]] = None,
  must_not: Option[Seq[Query]] = None,
  should: Option[Seq[Query]] = None,
  minimum_should_match: Option[Int] = None,
  boost: Option[Double] = None
)
