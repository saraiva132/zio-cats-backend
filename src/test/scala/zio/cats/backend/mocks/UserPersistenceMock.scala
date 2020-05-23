package zio.cats.backend.mocks

import zio.cats.backend.data.{User, UserId}
import zio.cats.backend.persistence.Persistence
import zio.test.mock.mockable

@mockable[Persistence.Service[UserId, User]]
object UserPersistenceMock
