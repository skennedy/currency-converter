package io.github.skennedy.currencyconverter

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.global

object ExchangeRateServiceSmokeTest extends IOApp {

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

  private[this] def service(client: Client[IO]): ExchangeRateService[IO] = {
    ExchangeRateService.impl(client)
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
