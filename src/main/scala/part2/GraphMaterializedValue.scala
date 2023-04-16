package part2

import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, Sink, Source}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object GraphMaterializedValue extends App {
  implicit val system = ActorSystem("system")

  def enhancedFlow[A, B](flow: Flow[A, B, _]): Flow[A, B, Future[Int]] = {
    val countSink = Sink.fold[Int, B](0)((acc, _) => acc + 1)

    Flow.fromGraph {
      GraphDSL.createGraph(countSink) { implicit builder => sinkShape =>
        import GraphDSL.Implicits._

        val flowShape = builder.add(flow)
        val broadcast = builder.add(Broadcast[B](2))
        //val sinkShape = builder.add(Sink.fold[Int, A](0)((acc, _) => acc + 1))

        flowShape ~> broadcast ~> sinkShape

        FlowShape(flowShape.in, broadcast.out(1))
      }
    }
  }

  val source = Source(1 to 10)
  val flow = Flow[Int].map(_ + 1)
  val sink = Sink.foreach[Int](println)

  val count = source.viaMat(enhancedFlow(flow))(Keep.right).to(sink).run

  import scala.concurrent.ExecutionContext.Implicits.global
  count.onComplete {
    case Success(value) => println(s"number of elems: $value")
    case Failure(exception) => println("failed")
  }
}
