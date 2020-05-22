package zio.cats.backend

import zio.{Has, Task}

package object persistence {

  type Persistence[I, O] = Has[Persistence.Service[I, O]]

  object Persistence {
    trait Service[I, O] {
      def retrieve(i: I): Task[Option[O]]
      def create(a: O): Task[O]
      def delete(i: I): Task[Boolean]
      def isHealthy : Task[Boolean]
    }
  }

}
