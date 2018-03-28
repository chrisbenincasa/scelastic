package com.chrisbenincasa.scelastic

import com.chrisbenincasa.scelastic.ast._
import com.chrisbenincasa.scelastic.builders.{BoolQueryBuilder, QueryBuilder, SearchBuilder}
import com.chrisbenincasa.scelastic.dsl._
import com.chrisbenincasa.scelastic.queries.{Query => ESQuery}
import com.chrisbenincasa.scelastic.queries.{MatchAllQuery, MatchNoneQuery}
import io.paradoxical.jackson.JacksonSerializer
import org.scalatest.FlatSpec

case class Document(
  Address: String,
  MaritalStatus: String,
  Age: Int
)

object Dsl extends Dsl
import com.chrisbenincasa.scelastic.Dsl._

class MacroTests extends FlatSpec {
  val serializer = JacksonSerializer.default

  val q = quote {
    search[Document].
      bool(
//        _.must.`match`(d => d.Address == "123")
        _.filter.term.query(_.Age == 3).boost(1.0f)
//        _.filter.term(_.Age == 3),
//        _.filter.range(_.Age)(_ >= 3)
      )
  }

  println(q.ast)

//  val j = ESJsonTranslator(q.ast)

//  println(serializer.writerWithDefaultPrettyPrinter().writeValueAsString(j))
}

object ESJsonTranslator {
//  def apply(query: Ast) = {
//    val q: ESQuery = query match {
//      case q: Query => q match {
//        case MatchAll => QueryBuilder().withBool(BoolQueryBuilder().addMust(MatchAllQuery).build).build
//        case MatchNone => QueryBuilder().withBool(BoolQueryBuilder().addMust(MatchNoneQuery).build).build
//        case Bool(a, b, c) => QueryBuilder().withBool(BoolQueryBuilder().addMust(apply(c)).build).build
//        case BoolMust(a, b) => QueryBuilder().withBool()
//      }
//      case Dynamic(_) => ???
//      case Block(_) => ???
//      case Ident(_) => ???
//    }
//
//    SearchBuilder().withQuery(q).build
//  }
//
//  private def apply(query: Query): Query = {
//    case MatchAll => QueryBuilder().withBool(BoolQueryBuilder().addMust(MatchAllQuery).build).build
//    case MatchNone => QueryBuilder().withBool(BoolQueryBuilder().addMust(MatchNoneQuery).build).build
//    case Bool(a, b, c) => QueryBuilder().withBool(BoolQueryBuilder().addMust(apply(c)).build).build
//    case BoolMust(a, b) => QueryBuilder().withBool()
//  }
}
