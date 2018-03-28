package com.chrisbenincasa.scelastic.dsl

trait MetaDslLowPriorityImplicits {
  this: MetaDsl =>

  implicit def materializeIndexMeta[T]: IndexMeta[T] = macro MetaDslMacro.materializeIndexMeta[T]
}

trait MetaDsl extends MetaDslLowPriorityImplicits { this: Dsl =>
  def indexMeta[T](entity: String, columns: (T => (Any, String))*): IndexMeta[T] = macro MetaDslMacro.indexMeta[T]

  trait IndexMeta[T] {
    def entity: Quoted[IndexSearch[T]]
  }
}
