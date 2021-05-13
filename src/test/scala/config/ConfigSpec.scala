package config

import cats.effect.IO
import io.movies.config.Configuration
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConfigSpec extends AnyFlatSpec with Matchers {
  "Configuration file" should "load" in {
    val config = Configuration.loadConfiguration[IO].unsafeRunSync()
    val expectedConfig = Configuration(
      "jdbc:postgresql://127.0.0.1:5432/",
      "postgres",
      "somePassword",
      "MovieService",
      "postgres",
      "org.postgresql.Driver")

    config shouldEqual(expectedConfig)
  }
}
