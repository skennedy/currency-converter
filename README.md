# currency-converter

REST API to convert currency values based on exchange rates from https://exchangeratesapi.io

## Style

I elected to use http4s as I am generally a fan of the pure functional flavour of Scala using 
tagless-final encoding, but I am just as experienced in akka ecosystem and happy working in either.

## Structure

The service classes encapsulate the simple core of the application:
- `ExchangeRateService`
  - returns exchange rate between two currencies
- `CurrencyConverterService`
  - given an amount in one currency, convert to amount in another currency

Then at the edges we have:
- `ExchangeRateApiClient` 
  - encapsulates HTTP client to https://exchangeratesapi.io
  - hasing caching implementation which uses scala-cache to cache the responses for 1 minute
- `CurrencyConverterRoutes`
  - defines the incoming API endpoint

Finally, `CurrencyConverterServer` instantiates everything including the http4s server and `Main` just runs it.

There are unit tests for the service classes, a higher level routes test that exercises most of the code end-to-end and also a smoke test that was used to test the 3rd party API.

## Future improvements
- Decouple the internal case class definitions from those that define the API endpoints
- More explicit error handling - rather than using the MonadError back channel, make the 3rd party API client return `F[Either[Error, A]]`

