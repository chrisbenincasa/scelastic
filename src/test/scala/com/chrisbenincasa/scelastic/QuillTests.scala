package com.chrisbenincasa.scelastic

import io.getquill._
import org.scalatest.FlatSpec

case class Person(name: String, age: Int)
class QuillTests extends FlatSpec {
  object testContext extends MirrorContext(MirrorIdiom, Literal)
  import testContext._

  val m = quote {
    query[Person].map(_.age).filter(_ >= 10).map(_ + 1)
  }

  println(m.ast)
}
