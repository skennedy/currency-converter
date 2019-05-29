package io.github.skennedy.currencyconverter

import cats.effect.IO
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers}

class CurrencyConverterServiceSpec extends FreeSpec with Matchers with TableDrivenPropertyChecks {

  val GBP = Currency("GBP")
  val USD = Currency("USD")

  val GBP_USD: BigDecimal = 1.26

  "CurrencyConverter" - {
    "returns converted amount" - {
      val exchangeRateService = exchangeRateServiceWithRate(GBP_USD)
      val converter           = CurrencyConverterService.impl[IO](exchangeRateService)

      val result = converter.convert(CurrencyConverterService.Request(GBP, USD, 50)).unsafeRunSync()
      result shouldBe CurrencyConverterService.ConvertedAmount(GBP_USD, 50 * GBP_USD, 50)
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
