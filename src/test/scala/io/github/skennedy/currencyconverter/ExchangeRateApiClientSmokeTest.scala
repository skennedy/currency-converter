package io.github.skennedy.currencyconverter

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.global

/**
  * A smoke test written to initially explore the API.
  * Implemented as a main method so it doesn't run as a unit test (because it hits real external API).
  */
object ExchangeRateApiClientSmokeTest extends IOApp {

  def run(args: List[String]) = {
    BlazeClientBuilder[IO](global).resource.use(
      client =>
        for {
          _ <- serviceReturnsRates(client, Currency("USD"))
          _ <- serviceReturnsRates(client, Currency("GBP"))
          _ <- serviceReturnsError(client, Currency("XXX"))
        } yield {
          ExitCode.Success
        }
    )
  }

  private[this] def service(client: Client[IO]): ExchangeRateApiClient[IO] = {
    ExchangeRateApiClient.impl(client)
  }

  private[this] def serviceReturnsRates(client: Client[IO], base: Currency) = {
    service(client).get(base).attempt.map { result =>
      assert(result.isRight)
    }
  }

  private[this] def serviceReturnsError(client: Client[IO], base: Currency) =
    service(client).get(base).attempt.map { result =>
      assert(result.isLeft)
    }
}
