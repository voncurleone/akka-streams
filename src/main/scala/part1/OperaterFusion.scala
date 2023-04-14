package part1

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}

object OperaterFusion extends App {
  implicit val system = ActorSystem("system")

  //fused
  /*Source(1 to 10)
    .map(e => { println(s"Flow1: $e") ; e})
    .map(e => { println(s"Flow2: $e") ; e})
    .map(e => { println(s"Flow3: $e") ; e})
    .runWith(Sink.ignore)*/

  //async boundaries
  Source(1 to 10)
    .map(e => { println(s"Flow1: $e"); e }).async
    .map(e => { println(s"Flow2: $e"); e }).async
    .map(e => { println(s"Flow3: $e"); e }).async
    .runWith(Sink.ignore)

  system.terminate()
}
