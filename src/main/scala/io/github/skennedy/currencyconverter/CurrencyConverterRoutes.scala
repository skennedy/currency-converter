package io.github.skennedy.currencyconverter

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object CurrencyConverterRoutes {

  def convertRoutes[F[_]: Sync](C: CurrencyConverterService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case httpReq @ POST -> Root / "api" / "convert" =>
        httpReq.decode[CurrencyConverterService.Request] { req =>
          for {
            result <- C.convert(req)
            resp   <- Ok(result)
          } yield resp
        }
    }
  }
}
