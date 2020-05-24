package zio.cats.backend.mocks

import zio.cats.backend.persistence.UserPersistence
import zio.test.mock.mockable

@mockable[UserPersistence.Service]
object UserPersistenceMock
