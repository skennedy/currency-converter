package io.github.skennedy.currencyconverter

import cats.Monad
import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import scalacache.CatsEffect.modes._
import scalacache.guava._

import scala.concurrent.ExecutionContext.global

object CurrencyConverterServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream

      guavaCache            = GuavaCache[ExchangeRateApiClient.ExchangeRates]
      exchangeRateApiClient = ExchangeRateApiClient.cachingImpl[F](client, guavaCache)
      exchangeRateService   = ExchangeRateService.impl[F](exchangeRateApiClient)
      currencyConverter     = CurrencyConverterService.impl[F](exchangeRateService)

      httpApp = CurrencyConverterRoutes.convertRoutes[F](currencyConverter).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .withServiceErrorHandler(errorHandler)
        .serve
    } yield exitCode
  }.drain

  def errorHandler[F[_]](implicit F: Monad[F]): ServiceErrorHandler[F] = req => {
    customErrorHandler[F](F)(req).orElse(DefaultServiceErrorHandler[F](F)(req))
  }

  def customErrorHandler[F[_]: Monad]: ServiceErrorHandler[F] = req => {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    {
      case ApiError.UnsupportedCurrency(c) =>
        BadRequest(s"Unsupported currency: ${c.code}")
    }
  }
}
