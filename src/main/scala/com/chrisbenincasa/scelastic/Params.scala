package com.chrisbenincasa.scelastic

object Builder {

}
object Params {
  private case class ParamsImpl(m: Map[Param[_], Any]) extends Params {
    def apply[P](implicit param: Param[P]): P = {
      m.get(param) match {
        case Some(v) => v.asInstanceOf[P]
        case None => param.default
      }
    }

    def optional[P](implicit param: Param[P]): Option[P] = {
      m.get(param).map(_.asInstanceOf[P])
    }

    override def contains[P](implicit param: Param[P]): Boolean =
      m.contains(param)

    override def iterator: Iterator[(Param[_], Any)] =
      m.iterator

    override def +[P](p: P)(implicit param: Param[P]): Params =
      copy(m + (param -> p))

    override def addAll(ps: Params): Params =
      copy(m ++ ps.iterator)
  }

  val empty: Params = ParamsImpl(Map.empty)
}

trait Params {
  def apply[P: Param]: P
  def optional[P: Param]: Option[P]
  def contains[P: Param]: Boolean
  def iterator: Iterator[(Param[_], Any)]
  def +[P: Param](p: P): Params
  def ++(ps: Params): Params = addAll(ps)
  def addAll(ps: Params): Params
}

trait Param[P] {
  def default: P

  def show(p: P): Seq[(String, () => String)] = Seq.empty
}

trait OptionalParam[T] { self: Param[T] => }

object Param {
  def apply[T](t: => T): Param[T] = new Param[T] {
    lazy val default: T = t
  }

  def optional[T]: Param[T] with OptionalParam[T] = new Param[T] with OptionalParam[T] {
    lazy val default: T = null.asInstanceOf[T]
  }
}

trait Parameterized[+T] {
  def params: Params
  def configured[P](p: P)(implicit param: Param[P]): T = withParams(params + p)
  def withParams(ps: Params): T
}

trait ParameterizedBuilder[T, Builds] extends Parameterized[T] {
  def build: Builds

  override def withParams(ps: Params): T =
    copy1(params = ps)

  protected def copy1(params: Params = this.params): T
}

