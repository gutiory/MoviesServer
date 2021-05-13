package movies

import cats.effect.{Blocker, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import doobie.Transactor
import doobie.util.ExecutionContexts
import org.scalatest.flatspec.AnyFlatSpec

trait UsePostgreSQL extends AnyFlatSpec with ForAllTestContainer {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  override val container: PostgreSQLContainer =
    PostgreSQLContainer("postgres", "MovieService", "postgres", "somePassword")

  lazy val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",     // driver classname
    container.jdbcUrl,
    container.username,                  // user
    container.password,                          // password
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )
}
