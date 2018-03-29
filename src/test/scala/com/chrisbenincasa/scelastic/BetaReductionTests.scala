package com.chrisbenincasa.scelastic

import org.scalatest.{FlatSpec, FunSpec, Matchers}
import com.chrisbenincasa.scelastic.ast._
import com.chrisbenincasa.scelastic.translate.TranslatorImplicits

class BetaReductionTests extends FlatSpec with Matchers {
  "BetaReduction" should "apply functions" in {
    val function = Function(Ident("a") :: Nil, Ident("a"))
    val ast: Ast = FunctionApply(function, Ident("b") :: Nil)
    BetaReduction(ast) shouldEqual Ident("b")
  }

  it should "do something" in {
    val definition = Val(Ident("b"), Constant(1))
    val function = Val(Ident("func"), Function(Ident("b") :: Nil, Ident("b")))
    val ast: Ast = FunctionApply(Ident("func"), Ident("b") :: Nil)
    val block: Ast = Block(definition :: function :: ast :: Nil)
    println(block)
    println(BetaReduction(block))
//    println(BetaReduction(Val(Ident("b"), Constant(1)) : Ast))
  }

  it should "double map" in {
//    val mm =
  }
}

class StatementInterpolatorTests extends FlatSpec with Matchers with TranslatorImplicits {


  "StatementInterpolator" should "work" in {
    val thing = tokens"thing"
    val stmt = tokens"$thing $thing $thing"
    println(stmt)
  }
}
