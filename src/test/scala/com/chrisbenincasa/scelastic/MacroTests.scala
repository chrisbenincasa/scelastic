package com.chrisbenincasa.scelastic

import com.chrisbenincasa.scelastic.ast._
import com.chrisbenincasa.scelastic.builders.SearchBuilder
import com.chrisbenincasa.scelastic.dsl._
import io.paradoxical.jackson.JacksonSerializer
import org.scalatest.FlatSpec

class MacroTests extends FlatSpec with Dsl {
  val serializer = JacksonSerializer.default

  val v = quote(1)
  val q = quote {
    search[Document].
      bool(
        _.filter.term(d => d.Age >= v),
        _.must.`match`(_.MaritalStatus == "Married", boost(2.0f))
      )
  }

//  val q2 = quote {
//    search[Document].
//      avg(_.Age)
//  }

//  println(q2.ast)

  val bool = AstToQueryParser.parse(q.ast)
  val s = SearchBuilder().withQuery(bool).build

  println(serializer.writerWithDefaultPrettyPrinter().writeValueAsString(s))
}

case class FlattenESQuery(
  query: Ast
)

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