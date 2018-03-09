package com.chrisbenincasa.scelastic.queries

trait Query
case class EmptyObject private (x: Option[Int] = None)
object EmptyObject {
  lazy val instance = EmptyObject()
}

trait MatchQueryLike extends Query
case object MatchAllQuery extends Map.Map1[String, EmptyObject]("match_all", EmptyObject.instance) with MatchQueryLike
case object MatchNoneQuery extends Map.Map1[String, EmptyObject]("match_none", EmptyObject.instance) with MatchQueryLike

// Use specialized Map.Map1 here for two reasons:
// 1. it preserves the value of the key during serialization, unlike Product/Tuple
// 2. Map1 is concrete, so we don't have to provide any implementations
trait MatchQueryClause
case class MatchQuery(`match`: MatchQueryClause) extends MatchQueryLike
case class MatchSimple(field: String, value: String) extends Map.Map1[String, String](field, value) with MatchQueryClause
case class MatchSimpleBody(query: String, operator: Option[String])
case class MatchComplex(body: MatchSimpleBody) extends Map.Map1[String, MatchSimpleBody]("match", body) with MatchQueryClause

trait TermQueryLike extends Query
trait TermQueryClause
case class TermQuery(term: TermQueryClause) extends TermQueryLike
case class TermSimple(field: String, value: String) extends Map.Map1[String, String](field, value) with TermQueryClause
case class TermQueryBody(value: String, boost: Double)
case class TermComplex(body: TermQueryBody) extends Map.Map1[String, TermQueryBody]("term", body) with TermQueryClause

trait TermsQueryClause
case class TermsQuery(terms: TermsQueryClause) extends Query
case class TermsSimple(field: String, terms: Seq[String]) extends Map.Map1[String, Seq[String]](field, terms) with TermsQueryClause
case class TermsQueryBody(field: String, terms: Seq[String]) extends Map.Map1[String, Seq[String]](field, terms) with TermQueryClause
case class TermsComplex(body: TermsQueryBody) extends Map.Map1[String, TermsQueryBody]("terms", body) with TermQueryLike


trait CompoundQuery[T] extends Query with Map[String, T]

case class BoolCompoundQuery(boolQueryStruct: BoolQueryStruct)
  extends Map.Map1[String, BoolQueryStruct]("bool", boolQueryStruct)
  with CompoundQuery[BoolQueryStruct]

trait JoinQuery extends Query

case class NestedQuery(
  path: String,
  score_mode: String,
  query: Query
)

case class NestedCompoundQuery(nestedQuery: NestedQuery)
  extends Map.Map1[String, NestedQuery]("nested", nestedQuery)
  with CompoundQuery[NestedQuery]
