package zio.cats.backend.mocks

import zio.cats.backend.services.reqres.reqres.ReqResClient
import zio.test.mock.mockable

@mockable[ReqResClient.Service]
object ReqResClientMock
