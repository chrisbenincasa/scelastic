package com.chrisbenincasa.scelastic.dsl

import com.chrisbenincasa.scelastic.ast.Ast
import scala.annotation.compileTimeOnly
import scala.reflect.macros.whitebox

private[dsl] trait QuotedDsl {
  trait Quoted[+T] {
    def ast: Ast
  }

  implicit def quote[T](body: T): Quoted[T] = macro QuoteMacroImpl.quote[T]

  @compileTimeOnly("bad")
  implicit def unquote[T](quoted: Quoted[T]): T = throw new RuntimeException
}

private[dsl] class QuoteMacroImpl(val c: whitebox.Context) extends QuoteMacro
