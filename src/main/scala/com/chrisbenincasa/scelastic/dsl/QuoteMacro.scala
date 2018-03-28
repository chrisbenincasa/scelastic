package com.chrisbenincasa.scelastic.dsl

import com.chrisbenincasa.scelastic.ast.Ast
import scala.annotation.StaticAnnotation
import scala.reflect.ClassTag
import scala.reflect.macros.whitebox

case class QuotedAst(ast: Ast) extends StaticAnnotation

private[dsl] class QuoteMacro(val c: whitebox.Context) extends Parsing with LiftableInstances with Unliftables {
  import c.universe._

  private val quoted = TermName("quoted")

  def quote[T](body: Tree)(implicit t: WeakTypeTag[T]) = {
    val ast = astParser(body)

    c.untypecheck {
      q"""
         new ${c.prefix}.Quoted[$t] {
            override def ast = $ast
         }
       """
    }
  }

  protected def unquote[T](tree: Tree)(implicit ct: ClassTag[T]) =
    astTree(tree).flatMap(astUnliftable.unapply).map {
      case ast: T => ast
    }

  private def astTree(tree: Tree) =
    for {
      method <- tree.tpe.decls.find(_.name == quoted)
      annotation <- method.annotations.headOption
      astTree <- annotation.tree.children.lastOption
    } yield astTree
}
