package com.chrisbenincasa.scelastic.dsl

import com.chrisbenincasa.scelastic.ast.Ast
import scala.annotation.compileTimeOnly

trait QuotedDsl {
  trait Quoted[+T] {
    def ast: Ast
  }

  implicit def quote[T](body: T): Quoted[T] = macro QuoteMacro.quote[T]

  @compileTimeOnly("bad")
  implicit def unquote[T](quoted: Quoted[T]): T = throw new RuntimeException
}
