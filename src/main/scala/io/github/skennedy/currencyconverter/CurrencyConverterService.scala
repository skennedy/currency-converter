package io.github.skennedy.currencyconverter

import cats.effect.Sync
import cats.implicits._

trait CurrencyConverterService[F[_]] {
  def convert(n: CurrencyConverterService.Request): F[CurrencyConverterService.ConvertedAmount]
}

object CurrencyConverterService {
  final case class Request(fromCurrency: Currency, toCurrency: Currency, amount: BigDecimal)
  final case class ConvertedAmount(exchange: BigDecimal, amount: BigDecimal, original: BigDecimal)

  def impl[F[_]: Sync](exchangeRateService: ExchangeRateService[F]): CurrencyConverterService[F] =
    new CurrencyConverterService[F] {
      def convert(req: Request): F[ConvertedAmount] =
        exchangeRateService
          .get(req.fromCurrency, req.toCurrency)
          .map(rate => ConvertedAmount(rate, req.amount * rate, req.amount))
    }
}
