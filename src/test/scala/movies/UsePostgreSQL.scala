package movies

import cats.effect.Blocker
import cats.effect.IO

import com.dimafeng.testcontainers.ForAllTestContainer
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie.Transactor
import doobie.util.ExecutionContexts
import org.scalatest.flatspec.AnyFlatSpec

trait UsePostgreSQL extends AnyFlatSpec with ForAllTestContainer {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous) //scalafix:ok

  override val container: PostgreSQLContainer =
    PostgreSQLContainer(
      "postgres",
      "MovieService",
      "postgres",
      "somePassword"
    ) //scalafix:ok

  lazy val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", // driver classname
    container.jdbcUrl,
    container.username,      // user
    container.password,      // password
    Blocker.liftExecutionContext(
      ExecutionContexts.synchronous
    )                        // just for testing
  )                          //scalafix:ok
}
