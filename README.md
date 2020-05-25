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

### Service

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







