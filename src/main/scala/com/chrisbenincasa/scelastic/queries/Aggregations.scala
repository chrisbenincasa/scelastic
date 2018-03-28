package com.chrisbenincasa.scelastic.queries

case class AggregationQuery(aggs: (String, Aggregation)*) extends Map[String, Aggregation] with Query {
  private val underlying = Map(aggs: _*)
  override def +[V1 >: Aggregation](kv: (String, V1)): Map[String, V1] = underlying + kv
  override def -(key: String): Map[String, Aggregation] = underlying - key
  override def get(key: String): Option[Aggregation] = underlying.get(key)
  override def iterator: Iterator[(String, Aggregation)] = underlying.iterator
}

trait Aggregation
abstract class BaseAggregation[T](name: String, body: T) extends Map.Map1[String, T](name, body) with Aggregation
case class AverageAggregation(agg: AverageAggregationBody) extends BaseAggregation[AverageAggregationBody]("avg", agg)
case class AverageAggregationBody(field: String)

case class CardinalityAggregation(agg: CardinalityAggregationBody) extends BaseAggregation[CardinalityAggregationBody]("cardinality", agg)
case class CardinalityAggregationBody(field: String)

case class StatsAggregation(agg: StatsAggregationBody) extends BaseAggregation[StatsAggregationBody]("stats", agg)
object StatsAggregation {
  def apply(field: String): StatsAggregation = StatsAggregation(StatsAggregationBody(field))
}
case class StatsAggregationBody(field: String)
