package com.chrisbenincasa.scelastic.dsl

import scala.annotation.compileTimeOnly

trait Dsl
  extends SearchDsl
  with QuotedDsl
  with MetaDsl

trait SearchDsl {
  def search[T]: IndexSearch[T] = macro SearchDslMacros.expandEntity[T]

  @compileTimeOnly("bad")
  def searchSchema[T](entity: String, columns: (T => (Any, String))*): IndexSearch[T] = throw new RuntimeException

  @compileTimeOnly("bad")
  def boost(value: Float): QueryOption = throw new RuntimeException

  @compileTimeOnly("bad")
  def operator(value: String): QueryOption = throw new RuntimeException

  sealed trait Search[+T] {
    def map[R](f: T => R): Search[R]

    def bool[R](f: (BoolSearchNode[T] => Search[R])*): Search[R]

    def avg[R](f: T => R): Search[T]
    def stats[R](f: T => R): Search[T]

    def drop(n: Int): Search[T]
    def take(n: Int): Search[T]
  }

  sealed trait IndexSearch[T] extends Search[T] {
    override def map[R](f: T => R): IndexSearch[R]
  }

  sealed trait BoolSearch[+T] extends Search[T]

  sealed trait BoolSearchNode[+T] extends BoolSearch[T] {
    def must: MustContext[T]
    def must_not: MustContext[T]
    def filter: FilterContext[T]

    def minimum_should_match(i: Int): BoolSearch[T]
    def boost(v: Float): BoolSearch[T]
  }

  sealed trait SearchContext[T]

  sealed trait MustContext[+T] extends BoolSearch[T] {
    def `match`(q: T => Boolean, options: QueryOption*): BoolSearch[T]
    def match_all: BoolSearch[T]
    def match_none: BoolSearch[T]

    def term(q: T => Boolean, options: QueryOption*): TermNode[T]

    def exists[R](f: T => R): BoolSearch[T]
  }

  sealed trait FilterContext[+T] extends BoolSearch[T] {
    def `match`(q: T => Boolean, options: QueryOption*): FilterContext[T]
    def term(q: T => Boolean, options: QueryOption*): TermNode[T]
    def range[R](f: T => R)(ranges: (R => Boolean)*): Search[T]
  }

  sealed trait MatchNode[+T] extends Search[T] {
    def query(f: T => Boolean): MatchNode[T]
  }

  sealed trait TermNode[+T] extends Search[T] {
    def query[R](f: T => Boolean): TermNode[T]
    def boost(f: Float): TermNode[T]
    def term(f: T => Boolean): TermNode[T]
  }

  sealed trait QueryOption
  sealed trait TermQueryOption extends QueryOption

}


