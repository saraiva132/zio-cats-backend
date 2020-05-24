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
* Unit tests that test business logic mocking out dependencies
* Integration tests including StartUp logic using ZLayer
* Containerization

### Service

To run the service or integration tests please make use of the script provided.

To run service:
`./run-service.sh -dpbr`

To run tests with a clean service 
`./run-service.sh -drt` or `./run-service.sh -dpbrt` if it has not been published locally yet

#### API

`host:port/users` for user API

`host:port/docs` for documentation

`host:port/health/alive` for aliveness check

`host:port/health/ready` for readiness check







