package com.chrisbenincasa.scelastic.dsl

import scala.annotation.compileTimeOnly

trait Dsl extends SearchDsl with QuotedDsl with MetaDsl

trait SearchDsl {
  def search[T]: IndexSearch[T] = macro SearchDslMacros.expandEntity[T]

  @compileTimeOnly("bad")
  def searchSchema[T](entity: String, columns: (T => (Any, String))*): IndexSearch[T] = throw new RuntimeException

  sealed trait Search[+T] {
    def map[R](f: T => R): Search[R]

//    def bool[R](f: BoolSearch[T] => BoolSearch[R]): BoolSearch[R]
    def bool[R](f: (BoolSearchNode[T] => Search[R])*): Search[R]

    def drop(n: Int): Search[T]
    def take(n: Int): Search[T]
  }

  sealed trait IndexSearch[T] extends Search[T] {
    override def map[R](f: T => R): IndexSearch[R]
  }

  sealed trait BoolSearch[+T] extends Search[T]

  sealed trait BoolSearchNode[+T] extends BoolSearch[T] {
    def must: MustContext[T]
    def filter: FilterContext[T]
    //    def must_not
    //    def should
  }

  sealed trait SearchContext[T]

  sealed trait MustContext[+T] extends BoolSearch[T] {
    def `match`(q: T => Boolean): BoolSearch[T]
    def match_all: BoolSearch[T]
    def match_none: BoolSearch[T]
  }

  sealed trait FilterContext[+T] extends BoolSearch[T] {
    def `match`(q: T => Boolean): FilterContext[T]
    def term(q: T => Boolean): Search[T]
    def term: TermNode[T]

    def range[R](f: T => R)(ranges: (R => Boolean)*): Search[T]
  }

  sealed trait TermNode[+T] extends Search[T] {
    def query[R](f: T => Boolean): TermNode[T]
    def boost(f: Float): TermNode[T]
  }
}


