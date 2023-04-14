package part2
import akka.actor.ActorSystem
import akka.stream.FlowShape

import akka.stream.scaladsl.{Flow, GraphDSL, Source, Sink}

object OpenGraph extends App {
  implicit val system = ActorSystem("system")

  val source = Source(1 to 10)

  val flow1 = Flow[Int].map(_ + 1)
  val flow2 = Flow[Int].map(_ * 10)

  val flowGraph = Flow.fromGraph {
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val shape1 = builder.add(flow1)
      val shape2 = builder.add(flow2)

      shape1 ~> shape2

      FlowShape(shape1.in, shape2.out)
    }
  }

  source.via(flowGraph).runWith(Sink.foreach[Int](println))
}
