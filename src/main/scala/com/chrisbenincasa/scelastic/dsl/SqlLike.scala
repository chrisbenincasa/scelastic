//package com.chrisbenincasa.scelastic.dsl
//
//import com.sun.org.apache.xpath.internal.ExpressionNode
//
//trait ExpressionNode
//trait QueryExpression extends ExpressionNode
//trait SearchExpression extends QueryExpression {
//  def and(expr: SearchExpression): SearchExpression
//}
//trait QueryNode extends ExpressionNode {
//  def must(expr: ExpressionNode): MustNode
//  def filter(expr: ExpressionNode): FilterNode
//  def select[R](clos: => R): Yield[R] = new BasicYield[R](this, () => clos)
//}
//trait MustNode extends QueryNode
//trait FilterNode extends QueryNode
//trait MustNotNode extends QueryNode
//
//trait Yield[R]
//class BasicYield[R](expr: ExpressionNode, yielder: () => R) extends Yield[R]
//
//trait SqlLike {
//  case class Typed[A](v: A){
//    def ===(right: Typed[A]): SearchExpression
//    def >=(right: Typed[A]): SearchExpression
//    def gte(right: Typed[A]): SearchExpression
//  }
//  trait TypedExpression[T]
//  implicit def stringToTyped(s: String) = Typed[String](s)
//  implicit def intToTyped(s: Int) = Typed[Int](s)
//
//  def `match`(b: => SearchExpression): QueryNode
//
//  def query: QueryNode
//}
//
//abstract class Query[T]
//
//class SimpleQuery1[T](idx: SearchableIndex[T]) extends Query[T] {
//
//}
//
//class SearchableIndex[T] {
//  def search[R](f: T => Yield[R]): Query[T] = {
//    new SimpleQuery1[T](this)
//  }
//}
//
//case class Document(
//  Address: String,
//  MaritalStatus: String,
//  Age: Int
//)
//
//object Test extends SqlLike {
//  val docs = new SearchableIndex[Document]
//  val p = docs.search(d => {
//    query.must(
//      `match`(d.Address === "PA" and (d.Age gte 2))
//    ).filter(
//      `match`(d.MaritalStatus === "Married")
//    ).select(d)
//  })
//}
