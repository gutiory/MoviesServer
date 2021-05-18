package io.movies.api

import io.movies.model._
import io.movies.services.MovieService
import cats.effect._
import cats.syntax.all._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze._
import org.http4s.circe._
import io.circe.generic.auto._

class MovieServiceHttp4s[F[_]: Sync: MovieService]
    extends Http4sDsl[F] {
  val ms: MovieService[F] = implicitly[MovieService[F]]
  implicit val decoder: EntityDecoder[F, Movie] = jsonOf[F, Movie]
  implicit val encoder: EntityEncoder[F, RegisteredMovie] = jsonEncoderOf[F, RegisteredMovie]
  implicit val encoderList: EntityEncoder[F, List[RegisteredMovie]] = jsonEncoderOf[F, List[RegisteredMovie]]

  lazy val MOVIE_SERVICE = "MovieService"
  lazy val root = Root / MOVIE_SERVICE

  val service: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> root / "movie" / id => {
      ms.getMovieById(id.toShort).flatMap(_.fold(NotFound(s"No movie found with ID $id"))(Ok(_)))
    }
    case GET -> root / "movies" => {
      ms.getMovies.flatMap(list =>
        if(list.nonEmpty) Ok(list)
        else NotFound("There are no Movies in the Data Base")
      )
    }
    case request@POST -> root / "movie" => {
      (for{
        movie <- request.as[Movie]
        rm <- ms.addMovie(movie)
        res <- Created(rm)
      } yield res).handleErrorWith(_ => InternalServerError("Movie could not be inserted"))
    }
    case _ => NotImplemented("You don't know how to use this thing")
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
