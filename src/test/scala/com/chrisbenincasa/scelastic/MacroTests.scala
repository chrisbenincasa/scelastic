package com.chrisbenincasa.scelastic

import com.chrisbenincasa.scelastic.ast._
import com.chrisbenincasa.scelastic.ast.normalize.Normalize
import com.chrisbenincasa.scelastic.builders.{BoolQueryBuilder, QueryBuilder, SearchBuilder}
import com.chrisbenincasa.scelastic.dsl._
import com.chrisbenincasa.scelastic.queries.{AggregationQuery, MatchAllQuery, MatchNoneQuery, Query => ESQuery}
import io.paradoxical.jackson.JacksonSerializer
import org.scalatest.FlatSpec

case class DoubleNestedDoc(
  deep: Boolean
)

case class NestedDocument(
  status: String,
  nested2: DoubleNestedDoc
)

case class Document(
  Address: String,
  MaritalStatus: String,
  Age: Int,
  Nested: NestedDocument
)

object Dsl extends Dsl
import Dsl._

class MacroTests extends FlatSpec {
  val serializer = JacksonSerializer.default

  val v = quote(1)
//  val q = quote {
//    search[Document].
//      bool(
//        _.filter.term(d => d.Age >= v).term(_.Address == "ASD"),
//        _.must.`match`(_.MaritalStatus == "Married", operator("and"))
//      )
//  }

  def theSearch(i: Int) = quote {
    search[Document].
      bool(
        _.filter.term(d => d.Age >= i).term(_.Address == "ASD"),
        _.must.`match`(_.MaritalStatus == "Married", operator("and")),
        _.must_not.exists(_.Nested.nested2.deep)
      )
  }

  val q = theSearch(10)
  PrintAst(q.ast)
//  val normal = Normalize(q.ast)
//  println(normal)

//  val j = ESJsonTranslator(q.ast)

//  println(serializer.writerWithDefaultPrettyPrinter().writeValueAsString(j))
}

case object PrintAst extends AstWalk {
  override val onNode: Ast => Unit = println(_)
}

case class FlattenESQuery(
  queries: List[ESQuery] = Nil,
  aggregations: List[AggregationQuery] = Nil,
  size: Option[Ast] = None,
  from: Option[Ast] = None,
)

object ESJsonTranslator {
//  def flatten(ast: Ast): FlattenESQuery = {
//    ast match {
//      case Bool(q, Ident(alias), body) =>
//        body match {
//          case BoolFilter(q1, body1) => FlattenESQuery()
//          case _ => ???
//        }
//      case _ => ???
//    }
//  }

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
