package zio.cats.backend.mocks

import zio.cats.backend.services.reqres.reqres.ReqRes
import zio.test.mock.mockable

@mockable[ReqRes.Service]
object ReqResClientMock
