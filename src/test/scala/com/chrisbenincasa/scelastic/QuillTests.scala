package com.chrisbenincasa.scelastic

import io.getquill._
import io.getquill.norm.Normalize
import org.scalatest.FlatSpec

object Ctx extends SqlMirrorContext(MirrorSqlDialect, Literal)
import Ctx._

case class Person(name: String, age: Int, opt: Option[Int])
class QuillTests extends FlatSpec {

  val v = quote(10)
//  val m = quote {
//    query[Person].filter(d => d.age >= v).map(_.age).filter(_ >= 10).take(10)
//  }

  def biggerThan(i: Float) = quote {
    query[Person].filter(r => r.age > lift(i))
  }

  val m = biggerThan(10)

  println(m.ast)
  val i = Ctx.run(m)
  println(i.ast)
}
