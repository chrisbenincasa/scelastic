package com.chrisbenincasa.scelastic.queries

import com.chrisbenincasa.scelastic.QueryBuilder

class DisMaxQuery[A <: QueryBuilder](self: QueryBuilder) {
  def tieBreaker(v: Double): A = ???
  def boost(v: Double): A = ???
}
