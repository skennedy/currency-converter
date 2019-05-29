package io.github.skennedy.currencyconverter

import cats.effect.IO
import io.github.skennedy.currencyconverter.ApiError.UnsupportedCurrency
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}

class ExchangeRateServiceSpec extends FreeSpec with Matchers with TableDrivenPropertyChecks {

  val GBP = Currency("GBP")
  val USD = Currency("USD")
  val EUR = Currency("EUR")

  val GBP_USD: BigDecimal = 1.26
  val GBP_EUR: BigDecimal = 1.13

  "ExchangeRateService" - {
    "returns correct rate" - {
      val service =
        ExchangeRateService.impl[IO](exchangeRateApiClientWithRates(USD -> GBP_USD, EUR -> GBP_EUR, GBP -> 1))

      forAll(
        Table(
          ("from", "to", "rate"),
          (GBP, USD, GBP_USD),
          (GBP, EUR, GBP_EUR),
          (GBP, GBP, 1)
        )
      ) {
        case (from, to, rate) =>
          val result = service.get(from, to).unsafeRunSync()
          result shouldBe rate
      }

    }

    "returns UnsupportedCurrency if api call fails" - {
      val service = ExchangeRateService.impl[IO]((base: Currency) => IO.raiseError(new RuntimeException("oops")))

      service.get(GBP, USD).attempt.unsafeRunSync() shouldBe Left(UnsupportedCurrency(GBP))
    }

    "returns UnsupportedCurrency if toCurrency unknown" - {
      val service = ExchangeRateService.impl[IO](exchangeRateApiClientWithRates(USD -> GBP_USD))

      service.get(GBP, EUR).attempt.unsafeRunSync() shouldBe Left(UnsupportedCurrency(EUR))
    }
  }

  private def exchangeRateApiClientWithRates(rates: (Currency, BigDecimal)*): ExchangeRateApiClient[IO] =
    (base: Currency) => {
      IO.pure(
        ExchangeRateApiClient.ExchangeRates(
          base,
          rates.toMap
        )
      )
    }
}
