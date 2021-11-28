package params

import play.api.mvc.QueryStringBindable

package object binders {
  implicit def queryStringBinder(
                                  implicit intBinder: QueryStringBindable[Int]
                                ): QueryStringBindable[Pager] =
    new QueryStringBindable[Pager] {
      override def bind(
                         key: String,
                         params: Map[String, Seq[String]]
                       ): Option[Either[String, Pager]] = {
        for {
          offset <- intBinder.bind(key + ".offset", params)
          limit <- intBinder.bind(key + ".limit", params)
        } yield {
          (offset, limit) match {
            case (Right(offset), Right(limit)) => Right(Pager(offset, limit))
            case _                             => Left("Unable to bind a Pager")
          }
        }
      }
      override def unbind(key: String, pager: Pager): String = {
        intBinder.unbind(key + ".offset", pager.offset) + "&" + intBinder
          .unbind(
            key + ".limit",
            pager.limit
          )
      }
    }

}
