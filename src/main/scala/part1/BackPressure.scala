package part1

import akka.actor.ActorSystem
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}

object BackPressure extends App {
  implicit val system = ActorSystem("system")

  val sourceFast = Source(1 to 1000)
  val sinkSlow = Sink.foreach[Int] { x =>
    Thread.sleep(1000)
    println(s"Sink: $x")
  }

  val flowSimple = Flow[Int].map { x =>
    println(s"Flow: $x")
    x
  }
  val flowBuffered = flowSimple.buffer(20, OverflowStrategy.dropHead)

  sourceFast.async
    .via(flowBuffered).async
    .to(sinkSlow)
    //.run()

  import scala.concurrent.duration._
  sourceFast.throttle(2, 1.second).runWith(Sink.foreach(println))
  //system.terminate()
}
