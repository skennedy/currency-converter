package io.github.skennedy.currencyconverter

sealed trait ApiError extends RuntimeException

object ApiError {

  final case class UnsupportedCurrency(currency: Currency) extends ApiError
}
