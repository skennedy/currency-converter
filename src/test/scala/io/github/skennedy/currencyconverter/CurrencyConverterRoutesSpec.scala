package io.github.skennedy.currencyconverter

import cats.effect.IO
import org.http4s._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}

class CurrencyConverterRoutesSpec
    extends FreeSpec
    with Matchers
    with TableDrivenPropertyChecks
    with Http4sClientDsl[IO] {

  val GBP = Currency("GBP")
  val USD = Currency("USD")

  val GBP_USD: BigDecimal = 1.26

  "CurrencyConverter" - {
    "returns converted amount" - {
      val exchangeRateService = exchangeRateServiceWithRate(GBP_USD)
      val converter           = CurrencyConverterService.impl[IO](exchangeRateService)
      val routes              = CurrencyConverterRoutes.convertRoutes(converter).orNotFound

      val request  = Method.POST(CurrencyConverterService.Request(GBP, USD, 50), uri"/api/convert").unsafeRunSync()
      val response = routes(request).unsafeRunSync()

      response.status shouldBe Status.Ok
      response.as[CurrencyConverterService.ConvertedAmount].unsafeRunSync().amount shouldBe 50 * GBP_USD
    }
  }

  private def exchangeRateServiceWithRate(rate: BigDecimal) =
    new ExchangeRateService[IO] {
      def get(from: Currency, to: Currency): IO[BigDecimal] = {
        IO.pure(
          rate
        )
      }
    }
}
