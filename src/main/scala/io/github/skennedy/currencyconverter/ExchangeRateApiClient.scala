package io.github.skennedy.currencyconverter

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import io.circe.{Encoder, Decoder, Json, HCursor}
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, EntityEncoder, Method, Uri, Request}
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.Method._
import org.http4s.circe._

trait ExchangeRateApiClient[F[_]] {
  def get(base: Currency): F[ExchangeRateApiClient.ExchangeRates]
}

object ExchangeRateApiClient {

  def impl[F[_]: Sync](C: Client[F]): ExchangeRateApiClient[F] = new ExchangeRateApiClient[F] {
    val dsl = new Http4sClientDsl[F] {}
    import dsl._
    def get(base: Currency): F[ExchangeRateApiClient.ExchangeRates] = {
      val request = GET(uri"https://api.exchangeratesapi.io/latest".withQueryParam("base", base.code))
      C.expect[ExchangeRates](request)
        .adaptError { case t => ExchangeRateError(t) }
    }
  }

  final case class ExchangeRates(base: Currency, rates: Map[Currency, BigDecimal])

  object ExchangeRates {
    implicit val decoder: Decoder[ExchangeRates] = deriveDecoder[ExchangeRates]
    implicit def entityDecoder[F[_]: Sync]: EntityDecoder[F, ExchangeRates] =
      jsonOf
    implicit val encoder: Encoder[ExchangeRates] = deriveEncoder[ExchangeRates]
    implicit def entityEncoder[F[_]: Applicative]: EntityEncoder[F, ExchangeRates] =
      jsonEncoderOf
  }

  final case class ExchangeRateError(e: Throwable) extends RuntimeException
}
