package com.chrisbenincasa.scelastic.dsl

import scala.reflect.macros.blackbox

class SearchDslMacros(val c: blackbox.Context) {
  import c.universe._

  def expandEntity[T : WeakTypeTag]: Tree = {
    q"${meta[T]("Index")}.entity"
  }

  private def meta[T](prefix: String)(implicit t: WeakTypeTag[T]): Tree = {
    val expanderTpe = c.typecheck(tq"com.chrisbenincasa.scelastic.dsl.MetaDsl#${TypeName(s"${prefix}Meta")}[$t]", c.TYPEmode)
    c.inferImplicitValue(expanderTpe.tpe, silent = true) match {
      case EmptyTree => c.abort(c.enclosingPosition, s"Can't find an implicit `${prefix}Meta` for type `${t.tpe}`")
      case tree      => tree
    }
  }
}
