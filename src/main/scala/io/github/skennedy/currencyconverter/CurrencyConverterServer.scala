package io.github.skennedy.currencyconverter

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import fs2.Stream
import scala.concurrent.ExecutionContext.global

object CurrencyConverterServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      exchangeRateApiClient = ExchangeRateApiClient.impl[F](client)
      exchangeRateService   = ExchangeRateService.impl[F](exchangeRateApiClient)
      currencyConverter     = CurrencyConverterService.impl[F](exchangeRateService)

      httpApp = CurrencyConverterRoutes.convertRoutes[F](currencyConverter).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
