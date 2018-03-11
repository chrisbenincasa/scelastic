package com.chrisbenincasa.scelastic

import com.chrisbenincasa.scelastic.builders.{QueryBuilder, SearchBuilder}
import com.chrisbenincasa.scelastic.queries._
import com.curalate.json.jackson._
import org.scalatest._

class Tests extends FlatSpec with Matchers {
  "A test" should "run" in {

    val serializer = new JacksonSerializer()

    val avg = AverageAggregation(AverageAggregationBody("Age"))
    val stats = StatsAggregation("Age")

    val query = BoolQueryBuilder().
      addMust(
        MatchQuery(MatchComplex("Address", "PA", Some("and")))
      ).
      addFilter(
        MatchQuery(MatchSimple("MaritalStatus", "Married"))
      ).
      build

    val search = SearchBuilder().
      withQuery(
        QueryBuilder().withBool(query).build
      ).
      withAggregations(
        AggregationQuery("avg_age" -> avg, "age_stats" -> stats)
      ).
      withSize(0).
      build

    println(serializer.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(search))
  }
}
