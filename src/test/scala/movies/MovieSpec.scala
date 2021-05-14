package movies

import cats.effect.IO
import org.scalatest.matchers.should.Matchers
import io.movies.interpreters.RepositoryImpl
import io.movies.model.{Movie, RegisteredMovie}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import java.time.Year

class MovieSpec extends UsePostgreSQL with Matchers
{
    implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
    val movie: Movie = Movie("title1", "director1", Year.of(1981))

    "Database" should "be initialized" in {
      val repo = RepositoryImpl.doobie[IO](xa)

      // Drop with container just started should be 0
      repo.dropMovieTable.unsafeRunSync() shouldEqual (0)

      // Create after drop, should be 0
      repo.createMovieTable.unsafeRunSync() shouldEqual (0)
    }

    "Requested list" should "be empty" in {
      val repo = RepositoryImpl.doobie[IO](xa)
      repo.getMovies.unsafeRunSync() shouldEqual(List())
    }

    "A movie" should "be added to the database" in {
      val repo = RepositoryImpl.doobie[IO](xa)
      repo.addMovie(movie).unsafeRunSync() shouldEqual 1
    }

    "Movie with id 1 in database" should "be equal to expected" in {
      val repo = RepositoryImpl.doobie[IO](xa)

      val regMovieOption: Option[RegisteredMovie] = repo.getMovieById(1).unsafeRunSync()
      val regMovie = regMovieOption.getOrElse(fail("No movie with id 1"))

      regMovie.title shouldEqual (movie.title)
      regMovie.director shouldEqual (movie.director)
      regMovie.releaseDate shouldEqual (movie.releaseDate)
    }

    "Request a movie with nonexistent" should "be equal to None" in {
      val repo = RepositoryImpl.doobie[IO](xa)

      val regMovieOption: Option[RegisteredMovie] = repo.getMovieById(2).unsafeRunSync()
      regMovieOption shouldEqual(None)
    }

    "List of movies" should "be equal to expected" in {
      val repo = RepositoryImpl.doobie[IO](xa)

      val movie2: Movie = Movie("title2", "director2", Year.of(1981))
      repo.addMovie(movie2).unsafeRunSync() shouldEqual 1

      val movie3: Movie = Movie("title3", "director3", Year.of(1900))
      repo.addMovie(movie3).unsafeRunSync() shouldEqual 1

      val movie4: Movie = Movie("title4", "director4", Year.of(1979))
      repo.addMovie(movie4).unsafeRunSync() shouldEqual 1

      val expectedMovieList: List[Movie] = List(movie, movie2, movie3, movie4)

      val list = repo.getMovies.unsafeRunSync()

      val storedList = list.map(rm => Movie(rm.title, rm.director, rm.releaseDate))
      storedList.toSet shouldEqual(expectedMovieList.toSet)
    }
}
