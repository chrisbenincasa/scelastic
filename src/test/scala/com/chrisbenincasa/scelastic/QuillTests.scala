package com.chrisbenincasa.scelastic

import io.getquill._
import io.getquill.norm.Normalize
import org.scalatest.FlatSpec

case class Person(name: String, age: Int)
class QuillTests extends FlatSpec {
  val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
  import ctx._

  val m = quote {
    query[Person].filter(_.age >= 10).map(_.age).filter(_ >= 10).take(10)
  }

  println(m.ast)
  println(Normalize(m.ast))
}
