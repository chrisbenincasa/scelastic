//package com.chrisbenincasa.scelastic.dsl
//
//trait SqlLike {
//  trait MustNode
//  trait MustNotNode
//  trait QueryNode
//  trait QueryNodeExpression
//  trait StartNode
//
//  case class Typed[A](v: A) {
//    def ===(right: Typed[A]): SearchExpression
//  }
//  trait TypedExpression[T]
//  implicit def stringToTyped(s: String) = Typed[String](s)
//
//  def `match`(b: => SearchExpression): MatchQueryNode
//}
//
//trait ExpressionNode
//trait QueryExpression extends ExpressionNode
//trait SearchExpression extends QueryExpression
//trait MatchQueryNode extends ExpressionNode {
//  def operator
//}
//
//class SimpleQuery1[T](idx: SearchableIndex[T]) {
//
//}
//
//class SearchableIndex[T] {
//  def search(f: T => QueryExpression) = {
//    new SimpleQuery1[T](this)
//  }
//}
//
//case class Document(
//  cuid: String,
//  network: String
//)
//
//object Test extends SqlLike {
//  val docs = new SearchableIndex[Document]
//  docs.search(d => {
//    `match`(d.cuid === "instagram.123.123")
//  })
//}
