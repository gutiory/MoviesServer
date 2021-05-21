package config

import cats.syntax.option._

import io.movies.config.Configuration
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class ConfigSpec extends AnyFlatSpec with Matchers {
  "Configuration file" should "load" in {
      val config = ConfigSource.default.load[Configuration].toOption
      val expectedConfig = Configuration(
      "jdbc:postgresql://127.0.0.1:5432/",
      "postgres",
      "somePassword",
      "MovieService",
      "postgres",
      "org.postgresql.Driver")

    config shouldEqual(expectedConfig.some)
  }
}
