package io.github.skennedy.currencyconverter

import io.circe.{Decoder, Encoder, KeyDecoder, KeyEncoder}

final case class Currency(code: String) extends AnyVal

object Currency {

  val isValidCode: String => Boolean = code => code.matches("[A-Z]{3}")

  def parse(code: String): Option[Currency] =
    Some(code).filter(isValidCode).map(Currency(_))

  implicit val encoder: Encoder[Currency] = Encoder.encodeString.contramap(_.code)
  implicit val decoder: Decoder[Currency] = Decoder.decodeString.emap(parse(_).toRight("Invalid currency code"))

  implicit val keyEncoder: KeyEncoder[Currency] = KeyEncoder.encodeKeyString.contramap(_.code)
  implicit val keyDecoder: KeyDecoder[Currency] = new KeyDecoder[Currency] {
    override def apply(key: String): Option[Currency] = parse(key)
  }
}
