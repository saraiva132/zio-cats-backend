package zio.cats.backend.system.dbtransactor

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import org.flywaydb.core.api.configuration.ClassicConfiguration
import zio.{Has, RIO, ZIO}
import zio.cats.backend.system.config._

object Migration {

  val migrate: RIO[Has[PostgresConfig], Unit] =
    dbConfig.flatMap { cfg =>
      ZIO.effect {
        val config = new ClassicConfiguration()
        config.setDataSource(cfg.url.value, cfg.user.value, cfg.password.value)
        config.setLocations(new Location("classpath:db/migration"), new Location("filesystem:db/migration"))
        val newFlyway = new Flyway(config)
        newFlyway.migrate()
      }.unit
    }

}
