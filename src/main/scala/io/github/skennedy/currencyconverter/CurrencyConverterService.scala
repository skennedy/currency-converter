package io.github.skennedy.currencyconverter

import cats.effect.Sync
import cats.implicits._
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._

trait CurrencyConverterService[F[_]] {
  def convert(n: CurrencyConverterService.Request): F[CurrencyConverterService.ConvertedAmount]
}

object CurrencyConverterService {

  def impl[F[_]: Sync](exchangeRateService: ExchangeRateService[F]): CurrencyConverterService[F] =
    new CurrencyConverterService[F] {
      def convert(req: Request): F[ConvertedAmount] =
        exchangeRateService
          .get(req.fromCurrency, req.toCurrency)
          .map(rate => ConvertedAmount(rate, req.amount * rate, req.amount))
    }

  final case class Request(fromCurrency: Currency, toCurrency: Currency, amount: BigDecimal)

  object Request {
    implicit val encoder: Encoder[Request] = deriveEncoder[Request]
    implicit val decoder: Decoder[Request] = deriveDecoder[Request]

    implicit def entityEncoder[F[_]: Sync]: EntityEncoder[F, Request] = jsonEncoderOf
    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, Request] = jsonOf
  }

  final case class ConvertedAmount(exchange: BigDecimal, amount: BigDecimal, original: BigDecimal)

  object ConvertedAmount {
    implicit val encoder: Encoder[ConvertedAmount] = deriveEncoder[ConvertedAmount]
    implicit val decoder: Decoder[ConvertedAmount] = deriveDecoder[ConvertedAmount]

    implicit def entityEncoder[F[_]: Sync]: EntityEncoder[F, ConvertedAmount] = jsonEncoderOf
    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, ConvertedAmount] = jsonOf
  }
}
