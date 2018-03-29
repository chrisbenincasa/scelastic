package com.chrisbenincasa.scelastic.ast

// Refs == "key references the evaluation of value
// Ex. Map(Ident(a) -> Ident(b)) => substitute "b" when we see "a"
case class BetaReduction(refs: Map[Ast, Ast]) extends Traversal {
  override def apply(ast: Ast): Ast = {
    ast match {
      // If we see a ref for the current AST, start a new reduction
      // With the substitution
      case a if refs.isDefinedAt(a) =>
        BetaReduction(refs - a - refs(a))(refs(a))

      case Function(params, body) =>
        // Resolve refs from the function params, or return the param itself
        val mappedParams = params.map(p =>
          refs.get(p).collect { case i: Ident => i }.getOrElse(p)
        )

        // Reduce the function body with the newly resolved refs
        Function(mappedParams, BetaReduction(refs ++ params.zip(mappedParams))(body))

      case FunctionApply(Function(params, body), applicants) =>
        // Map function applicants to temporary reference so they don't conflict with fresh references within the function body
        val uniqApplicants = applicants.flatMap(CollectAst.byType[Ident]).map(i => i -> Ident(s"temp_${i.name}")).toMap[Ast, Ast]

        // Map function parameter names to the temporary references from before
        val mappedParams = params.map(p => uniqApplicants.getOrElse(p, p))

        // Reduce the body of the function using a map of the temporary references and a map of the original function references
        // to their "mapped" reference
        val reducedBody = BetaReduction(uniqApplicants ++ params.zip(mappedParams))(body)

        // Finally, reduce the function application
        apply(BetaReduction(refs ++ mappedParams.zip(applicants))(reducedBody))

      case Block(statements) =>
        // Resolve refs in reverse to account for any value defs, etc
        val (_, resolvedAst) = statements.reverse.tail.foldLeft(Map.empty[Ast, Ast] -> statements.last) {
          case ((refsAccum, statement), line) =>
            BetaReduction(refsAccum)(line) match {
              case Val(name, body) =>
                // Add the val definition to the refs map
                val refsWithVal = refs + (name -> body)

                // Reduce this statement body with the new val definition added
                val reducedStatement = BetaReduction(refsWithVal)(statement)

                refsWithVal -> reducedStatement
              case _ =>
                refs -> statement
            }
        }

        apply(resolvedAst)

      // Identity
      case other =>
        super.apply(other)
    }
  }

  override def apply(q: Query): Ast = {
    q match {
      case Bool(a, b, c) =>
        Bool(apply(a), b, BetaReduction(refs - b)(c))

      case BoolMust(a, b) =>
        BoolMust(apply(a), BetaReduction(refs)(b))

      case BoolFilter(a, b) =>
        BoolFilter(apply(a), BetaReduction(refs)(b))

      case _ =>
        super.apply(q)
    }
  }
}

object BetaReduction {
  def apply(ast: Ast, refs: (Ast, Ast)*): Ast = {
    BetaReduction(refs.toMap)(ast)
  }
}
