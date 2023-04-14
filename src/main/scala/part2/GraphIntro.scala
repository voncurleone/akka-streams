package part2

import akka.actor.ActorSystem
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Balance, Broadcast, GraphDSL, Merge, RunnableGraph, Sink, Source, Zip}

object GraphIntro extends App {

  implicit val system = ActorSystem("system")

  def out(msg: String) = println(s"[${Thread.currentThread()}] $msg")

  val source = Source(1 to 1000)
  val sink1 = Sink.foreach[Int] { x => out(s"sink1: $x") }
  val sink2 = Sink.foreach[Int] { x => out(s"sink2: $x") }

  val graph = RunnableGraph.fromGraph {
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[Int](2))

      source ~> broadcast

      //implicit port numbering
      broadcast ~> sink1
      broadcast ~> sink2

      //explicit port numbering
      //broadcast.out(0) ~> sink1
      //broadcast.out(1) ~> sink2

      ClosedShape
    }
  }

  //graph.run()

  import scala.concurrent.duration._
  val slowSource = source.throttle(2, 1.second).map(_ * -1)//= source.map( x => { Thread.sleep(1000); x * -1 })
  val fastSource = source.throttle(5, 1.second)

  val graph2 = RunnableGraph.fromGraph {
    GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._

      val merge = builder.add(Merge[Int](2))
      val balance = builder.add(Balance[Int](2))

      fastSource ~> merge
      slowSource ~> merge

      merge ~> balance

      balance ~> sink1
      balance ~> sink2

      ClosedShape
    }
  }

  graph2.run()
}
