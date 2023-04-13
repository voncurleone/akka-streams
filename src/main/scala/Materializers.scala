import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object Materializers extends App {
  implicit val system: ActorSystem = ActorSystem("system")

  val sourceSimple = Source( 1 to 10)
  val sinkLast = Sink.last[Int]

  val elem = sourceSimple.toMat(sinkLast)(Keep.right).run()
  elem.onComplete {
    case Success(value) => println(value)
    case Failure(e) => println(e)
  }

  val source = Source(List("hello there", "my name is zim", "how are you"))
  val countFlow = Flow[String].map(_.split(" ").length)
  val sink = Sink.reduce[Int](_ + _)

  val sum = source.viaMat(countFlow)(Keep.right).toMat(sink)(Keep.right).run()
  sum.onComplete {
    case Success(value) => println(value)
    case Failure(e) => println(e)
  }

  system.terminate()
}
