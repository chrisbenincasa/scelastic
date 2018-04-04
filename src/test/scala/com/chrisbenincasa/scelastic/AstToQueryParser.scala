package com.chrisbenincasa.scelastic

import com.chrisbenincasa.scelastic.ast._
import com.chrisbenincasa.scelastic.ast.{TermQuery => TermQueryAst, MatchQuery => MatchQueryAst}
import com.chrisbenincasa.scelastic.builders.BoolQueryBuilder

object AstToQueryParser {
  import com.chrisbenincasa.scelastic.queries._

  def parse(ast: Ast): Query = {
    ast match {
      case Bool(a, b, c) =>
        val queries = c.foldLeft(BoolQueryBuilder()) {
          case (a, b) =>
            parse(b) match {
              case q: BoolCompoundQuery => a.merge(q.builder)
              case _ => ???
            }
        }

        queries.build

      case TermQueryAst(a, b, c, d) =>
        val query = c match {
          case BinaryOperation(Property(_, name), EqualityOperator.`==`, Constant(v)) =>
            TermQuery(TermComplex(name, v))

          case BinaryOperation(Property(_, name), RangeOperator.`>=`, Constant(v)) =>
            v match {
              case _: Int | _: Long | _: Double | _: Float =>
                RangeQuery(name, RangeQueryBody(gte = Some(v.asInstanceOf[Int])))
              case _ => ???
            }

          case _ => ???
        }

        val out = a match {
          case _: BoolMust => BoolQueryBuilder().withMust(query)
          case _: BoolFilter => BoolQueryBuilder().withFilter(query)
          case _: BoolMustNot => BoolQueryBuilder().withMustNot(query)
          case _ => ???
        }

        out.build

      case MatchQueryAst(a, b, c, d) =>
        val builderWithOpts = parseOptions(d) {
          case (TermQueryOption.boost(Constant(v: Float)), builder) => builder.withBoost(v)
        }

        val query = c match {
          case BinaryOperation(Property(_, name), EqualityOperator.`==`, Constant(v)) =>
            val operator = d.collectFirst {
              case TermQueryOption.operator(Constant(v: String)) => v
            }

            MatchQuery(MatchComplex(name, v, operator))

          case _ => ???
        }

        val out = a match {
          case _: BoolMust => builderWithOpts.withMust(query)
          case _: BoolFilter => builderWithOpts.withFilter(query)
          case _: BoolMustNot => builderWithOpts.withMustNot(query)
          case _ => ???
        }

        out.build

      case _ => ???
    }
  }

  def parseOptions(l: List[Ast])(accept: PartialFunction[(OptionParam, BoolQueryBuilder), BoolQueryBuilder], zero: BoolQueryBuilder = BoolQueryBuilder()) = {
    l.foldLeft(zero) {
      case (accum, OptionParam(param, _)) => accept.lift.apply(param -> accum).map(_.merge(accum)).getOrElse(accum)
      case (accum, _) => accum
    }
  }
}
