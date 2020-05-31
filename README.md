## zio-cats-backend

A complete backend service integrating ZIO with:

* Cats
* Tapir
* STTP
* Http4s
* Doobie 
* ZIO-logging
* Pureconfig
* Circe
* Flyway

### Rational
The idea of this project is to showcase how to leverage the ZIO library together with really well designed 
libraries in the scala ecosystem which, although not designed for ZIO itself,
they abstracted over the effect implementation.
 
I have been asked multiple times questions such as:

* How do I use ZIO Layers?
* How do I test using ZIO?
* How do I write a service with ZIO without having to put types all over the place?
* How do I structure my code? 
 
This service tries to follow the best coding practices and showcase as much as one can leverage with the current ecosystem and ZIO, featuring:

* Type safe configuration
* Type safe logging
* Type safe database access (run integration tests to do query check)
* Unit tests that safely test business logic mocking out dependencies (zio-mock)
* Integration tests including before/after logic emulated with ZLayer
* Containerization
* Schema migration

### Service Architecture and code design

I took quite an opinionated path towards architecting this service.

The service is divided into 3 layers:

* **Routes (Input / Output)**
  * Routes are described as immutable values using ztapir. 
  Each endpoint is described with an input, output, error, and its dependencies.
  
* **Business Services (Transformations)**
  * A service in ZIO is called a module. Business services are modules that are typically specialized.
    There is no need for abstracting here, so, for that reason, I chose to leak implementation details
    and each method already defines its own dependencies in the method signature of the service.
    i.e. `def getUser(userId: UserId): RIO[UserPersistence, User]` 
    The advantage of this approach is its simplicity and how easy it is to wire dependencies. 
    The disadvantage is that it leads to methods having huge type signatures which forces us to carefully choose type aliases.
    
* **Services that interact with the outside world (Output / Input)**
  * Unlike business services. Services that interface with the outside world often require us
  to switch implementations. For that reason, the service definition does not leak implementation details
  so we leave that to the actual implementation, which can be described as a `final class` having its dependencies
  passed as constructor parameters which will, in turn, be a dependency requirement of the layer.
  i.e. `URLayer[Has[ReqResConfig] with SttpClient, ReqResClient]`
  The advantage of this approach is that your service API does not leak implementation details and is more flexible.


Disclaimer: Given the simplicity of the service. 
Our client that interacts with the third-party service is under `services`. 
I would normally put it under a package like `external` or `clients`.
`Persistence` already implies outside world interaction and can be seen as a specialized client (db).

Final note: From what I have seen so far. The most common and popular approach is to **not leak implementation details** 
and therefore use the second approach described. This will make the dependency graph more clear when wiring dependencies 
instead of basically passing all the services into a single effect that depends on everything.

### Commands

To run the service or integration tests please make use of the script provided.

To run service:
`./run-service.sh -dpbr`

To run tests with a clean service: 
`./run-service.sh -drt`

To run tests with a clean service that has not been published yet:
`./run-service.sh -dpbrt` 

To run tests: 
`./run-service.sh -t` 

#### API

User: `host:port/users` 

Docs: `host:port/docs` 

Aliveness check: `host:port/health/alive` 

Readiness check: `host:port/health/ready` 







