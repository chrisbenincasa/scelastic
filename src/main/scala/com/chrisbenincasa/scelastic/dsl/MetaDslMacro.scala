package com.chrisbenincasa.scelastic.dsl

import scala.reflect.macros.blackbox

class MetaDslMacro(val c: blackbox.Context) {
  import c.universe._

  def indexMeta[T](entity: Tree, columns: Tree*)(implicit t: WeakTypeTag[T]): Tree = {
    c.untypecheck {
      q"""
        new ${c.prefix}.IndexMeta[$t] {
          private[this] val _entity = ${c.prefix}.quote {
            ${c.prefix}.searchSchema[$t]($entity, ..$columns)
          }
          def entity = _entity
        }
      """
    }
  }

  def materializeIndexMeta[T](implicit t: WeakTypeTag[T]): Tree =
    if (t.tpe.typeSymbol.isClass && t.tpe.typeSymbol.asClass.isCaseClass) {
      q"""
          new ${c.prefix}.IndexMeta[$t] {
            private[this] val _entity =
              ${c.prefix}.quote(
                ${c.prefix}.searchSchema[$t](
                  ${t.tpe.typeSymbol.name.decodedName.toString}
                )
            )

            def entity = _entity
          }
        """
    } else {
      c.abort(c.enclosingPosition, s"Can't materialize a `IndexMeta` for non-case-class type '${t.tpe}', please provide an implicit `IndexMeta`.")
    }
}
