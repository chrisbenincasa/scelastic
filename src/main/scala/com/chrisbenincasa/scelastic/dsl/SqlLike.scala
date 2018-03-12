//package com.chrisbenincasa.scelastic.dsl
//
//import com.chrisbenincasa.scelastic.dsl.Types.{Typed, TypedField}
//import com.chrisbenincasa.scelastic.queries.BoolQueryBuilder
//import scala.reflect.macros.blackbox
//
//trait ExpressionNode
//trait QueryExpression extends ExpressionNode
//trait SearchExpression extends QueryExpression
//
//class BinarySearchExpression[A, B](val left: Typed[A], val right: Typed[B])(implicit c: Comparable[A, B]) extends SearchExpression
//
//trait QueryNode extends ExpressionNode {
//  def must(expr: SearchExpression): MustNode = {
//    BoolQueryBuilder().addMust()
//    ???
//  }
//  def filter(expr: ExpressionNode): FilterNode
//  def select[R](clos: => R): Yield[R] = new BasicYield[R](this, () => clos)
//}
//
//trait MustNode extends QueryNode
//trait FilterNode extends QueryNode
//trait MustNotNode extends QueryNode
//
//trait Yield[R]
//class BasicYield[R](expr: ExpressionNode, yielder: () => R) extends Yield[R]
//
//object TypedMacros {
//  def equals_macro[A, B](left: Typed[A], right: Typed[B]): Any = macro TypedMacros.equals_macro_impl[A, B]
//
//  def equals_macro_impl[A, B](c: blackbox.Context)(left: c.Expr[Typed[A]], right: c.Expr[Typed[B]]): c.Tree = {
//    import c.universe._
//    println(showRaw(left.tree))
//    left.tree
//  }
//}
//
//object Types {
//  sealed class Comparable[A, B]
//  type ~[A, B] = Comparable[A, B]
//
//  trait Typed[A] extends ExpressionNode {
//    def search[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = ???
//
//    def fuzzy[B](right: Typed[B], operator: String)(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = ???
//    def fuzzy[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = fuzzy(right, "and")
//
//    def phrase[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = ???
//
//    def ===[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = {
//      new BinarySearchExpression[A, B](this, right)
//    }
//    def !==[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = ???
//    def >=[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = gte(right)
//    def >[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = gt(right)
//    def <=[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = lte(right)
//    def <[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = lt(right)
//    def gte[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = ???
//    def lte[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = ???
//    def gt[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = ???
//    def lt[B](right: Typed[B])(implicit cc: Typed[A] ~ Typed[B]): SearchExpression = ???
//  }
//
//  trait TypedField[A] extends Typed[A] {
//    val field: String
//  }
//
//  trait TypedConstant[A] extends Typed[A] {
//    val value: A
//  }
//
//  trait NumericTyped[A] extends Typed[A] with NumericTypedLike
//  case class TInt(value: Int) extends TypedConstant[Int] with NumericTyped[Int]
//  case class TLong(value: Long) extends TypedConstant[Long] with NumericTyped[Long]
//
//  trait TypedExpression[T]
//
//  trait NumericTypedLike
//
//  trait Implicits {
//    implicit val stringType = new Typed[String] {}
//    implicit def stringToTyped(s: String) = new TypedConstant[String] { override val value = s }
//    implicit def intToTyped(s: Int) = TInt(s)
//    implicit def longToTyped(s: Long) = TLong(s)
//    implicit def sameTypeCompare[A] = new Comparable[A, A]
//    implicit def numericCompare[A : Numeric, B : Numeric] = new Comparable[Typed[A], Typed[B]]
//  }
//
//  object Implicits extends Implicits
//}
//
//trait SqlLike extends Types.Implicits {
//
//  def `match`(b: => SearchExpression): QueryNode = {
//    ???
//  }
//
//  def query: QueryNode = ???
//}
//
//object SqlLike extends SqlLike
//
//abstract class Query[Index <: SearchableIndex[_], Document]
//
//class SearchableIndex[T] {
//  final type DocumentType = T
//}
//
//class SearchQuery[T <: SearchableIndex[_]] extends Query[T, T#DocumentType] {
//  def search(f: T => SearchExpression) = ???
//}
//
//object SearchQuery {
//  def apply[T <: SearchableIndex[_]]: SearchQuery[T] = new SearchQuery[T]
//}
//
//case class Document(
//  Address: String,
//  MaritalStatus: String,
//  Age: Int
//)
//
//class DocumentIndex extends SearchableIndex[Document] {
//  import Types.Implicits._
//
//  def field[T](n: String)(implicit tt: Typed[T]): Typed[T] = new TypedField[T] {
//    override val field: String = n
//  }
//
//  def Address = field[String]("Address")
//}
//
