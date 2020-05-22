package zio.cats.backend

import zio.{Has, Task}

package object persistence {
  object Persistence {
    trait Service[I, O] {
      def get(i: I): Task[O]
      def create(a: O): Task[O]
      def delete(i: I): Task[Boolean]
    }
  }

  type Persistence[I,O] = Has[Persistence.Service[I, O]]

}
