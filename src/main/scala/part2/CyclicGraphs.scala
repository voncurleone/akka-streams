package part2

import akka.actor.ActorSystem
import akka.stream.FlowShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, MergePreferred, Sink, Source}

object CyclicGraphs extends App {
  implicit val system = ActorSystem("system")

  val fibStaticGraph = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val broadcast = builder.add(Broadcast[(Int, Int)](2))
    val tupleFlow = builder.add(Flow[Int].map(x => (x, x)))
    val merge = builder.add(MergePreferred[(Int, Int)](1))

    val prepFlow = builder.add(Flow[(Int, Int)].map{ case (x, y) => Thread.sleep(100); (y, x + y) })
    val retFlow = builder.add(Flow[(Int, Int)].map{ case (_, y) => y })

    tupleFlow ~> merge ~> broadcast ~> retFlow
    merge.preferred <~ prepFlow <~ broadcast

    FlowShape(tupleFlow.in, retFlow.out)
  }

  val source = Source(List(1))

  source.via(fibStaticGraph).to(Sink.foreach[Int](println)).run()
}
