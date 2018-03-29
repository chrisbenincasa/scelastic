package com.chrisbenincasa.scelastic.translate

trait Translator[T] {
  def translate(v: T): Token
}

object Translator {
  def apply[T](f: T => Token): Translator[T] =
    new Translator[T] {
      override def translate(v: T): Token = f(v)
    }
}

trait TranslatorImplicits extends TokenInterpolator {
  implicit class TranslatorImplicit[T](v: T)(implicit translator: Translator[T]) {
    def translate = translator.translate(v)
  }

  implicit val stringTranslator: Translator[String] =
    new Translator[String] {
      override def translate(v: String): Token = StringToken(v)
    }

  implicit val stringTokenTranslator: Translator[StringToken] = Translator(identity)
  implicit val statementTokenTranslator: Translator[Statement] = Translator(identity)

  implicit class TokenList[T](l: List[T])(implicit translator: Translator[T]) {
    def toStatement(separator: String = ", ") = {
      val tokens = l.map(_.translate)
      val separators = List.fill(tokens.length)(StringToken(separator))
      val statement = tokens.zip(separators).flatMap { case (token, sep) => token :: sep :: Nil }.dropRight(1)
      Statement(statement)
    }
  }

  implicit def listTranslator[T](implicit translator: Translator[T]): Translator[List[T]] = {
    Translator[List[T]](_.toStatement())
  }
}

object TranslatorImplicits extends TranslatorImplicits

trait TokenInterpolator {
  private val flattenStatements: List[Token] => List[Token] = (tokens: List[Token]) => {
    tokens.foldLeft(List.empty[Token]) {
      case (acc, Statement(stTokens)) =>
        acc ++ flattenStatements(stTokens)
      case (acc, tok) =>
        acc :+ tok
    }
  }

  private val greedyStringMerge = (tokens: List[Token]) => {
    val (tokensOut, merged) = tokens.foldLeft((List.empty[Token], StringToken("") : Token)) {
      case ((accum, tok : StringToken), next: StringToken) =>
        accum -> StringToken(s"${tok.string}${next.string}")
      case ((accum, tok), next) =>
        (accum :+ tok) -> next
    }

    tokensOut :+ merged
  }

  private val removeEmpties = (tokens: List[Token]) => tokens.filterNot(_ == StringToken(""))

  private val flatten = flattenStatements andThen greedyStringMerge andThen removeEmpties

  implicit class Impl(sc: StringContext) {
    def tokens(args: Token*): Statement = {
      sc.checkLengths(args)
      val parts = sc.parts.iterator
      val res = args.iterator.foldLeft(List[Token](StringToken(parts.next()))) {
        case (accum, arg) => accum :+ arg :+ StringToken(parts.next())
      }

      Statement(flatten(res))
    }
  }
}

object TokenInterpolator extends TokenInterpolator

sealed trait Token

case class StringToken(string: String) extends Token {
  override def toString = string
}

case class Statement(tokens: List[Token]) extends Token {
  override def toString = tokens.mkString
}
