package io.github.skennedy.currencyconverter

import cats.ApplicativeError.liftFromOption
import cats.effect.Sync
import cats.implicits._

trait ExchangeRateService[F[_]] {
  def get(from: Currency, to: Currency): F[BigDecimal]
}

object ExchangeRateService {

  def impl[F[_]: Sync](C: ExchangeRateApiClient[F]): ExchangeRateService[F] = new ExchangeRateService[F] {
    def get(from: Currency, to: Currency): F[BigDecimal] =
      for {
        ra   <- C.get(from).adaptError { case _ => ApiError.UnsupportedCurrency(from) }
        rate <- liftFromOption[F](ra.rates.get(to), ApiError.UnsupportedCurrency(to))
      } yield rate
  }

}
