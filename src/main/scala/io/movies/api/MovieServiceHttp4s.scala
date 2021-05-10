package io.movies.api

import io.movies.model._
import cats.effect._
import cats.syntax.all._
import io.movies.services.MovieService
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze._
import org.http4s.circe._
import io.circe.generic.auto._

class MovieServiceHttp4s[F[_]: Sync: MovieService]
(implicit monadThrow: MonadThrow[F])
    extends Http4sDsl[F] {
  val ms: MovieService[F] = implicitly[MovieService[F]]
  implicit val decoder: EntityDecoder[F, Movie] = jsonOf[F, Movie]
  implicit val encoder: EntityEncoder[F, RegisteredMovie] = jsonEncoderOf[F, RegisteredMovie]

  val MOVIESERVICE = "MovieService"
  val CREATE = "Create"
  val READ = "Read"
  val root = Root / MOVIESERVICE

  val service: HttpRoutes[F] = HttpRoutes.of[F] {
    case request@GET -> root / READ / id => {
      ms.getMovieById(id.toShort).flatMap(_.fold(NotFound())(Ok(_)))
    }
    case request@POST -> root / CREATE => {
      val temp: F[Int] = request.as[Movie].flatMap(movie => ms.addMovie(movie))
      temp.flatMap(res =>
        if (res != 1) {
          InternalServerError()
        }
        else {
          Created()
        }
      )
    }
    case _ => NotImplemented("You dont know how to use this thing")
  }

  def stream(args: List[String])
            (implicit ce: ConcurrentEffect[F], t: Timer[F]): F[ExitCode] =
    BlazeBuilder[F]
      .bindHttp(8080, "localhost")
      .mountService(service, "/")
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
