//package com.chrisbenincasa.scelastic
//
//import com.chrisbenincasa.scelastic.dsl._
//
//object MacroTests extends SqlLike{
//  val docs = new DocumentIndex
//  val d = Document("123", "Married", 12)
//  def show() = println(TypedMacros.equals_macro(docs.Address, "123"))
//
//  SearchQuery[DocumentIndex].search(doc => {
//    doc.Address === "PA"
//  })
//
//  //  val p = docs.search(d => {
//  //    query.must(
//  //      `match`(d.Address === "PA" and (d.Age gte 2L))
//  //    ).filter(
//  //      `match`(d.MaritalStatus === "Married")
//  //    ).select(d)
//  //  })
//}
