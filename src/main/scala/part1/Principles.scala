package part1

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

object Principles extends App {
  implicit val system: ActorSystem = ActorSystem("system")

  val names = List("tim", "reed", "christopher", "josh", "joseph", "william")

  val nameSource = Source(names)

  val longNameFlow = Flow[String].filter(_.length > 5)
  val firstTwoFlow = Flow[String].take(2)

  val sink = Sink.foreach(println)

  val graph = nameSource.via(longNameFlow).via(firstTwoFlow).to(sink)

  graph.run()

  system.terminate()
}
