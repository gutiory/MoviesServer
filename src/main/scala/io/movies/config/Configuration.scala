package io.movies.config

import cats.effect.Sync
import pureconfig._
import pureconfig.generic.auto._

case class Port(number: Int) extends AnyVal

case class Configuration(
    url: String,
    username: String,
    password: String,
    database: String,
    dockerImage: String,
    driverClassName: String
  )

object Configuration {
  def loadConfiguration[F[_]: Sync]: F[Configuration] = Sync[F].delay(ConfigSource.default.loadOrThrow[Configuration])
}

