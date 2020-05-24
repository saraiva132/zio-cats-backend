package zio.cats.backend

object TestData {

  private val serverEndpoint = "http://localhost:9001"
  val usersEndpoint          = s"$serverEndpoint/users"
  val healthCheckEndpoint    = s"$serverEndpoint/health/ready"

}
