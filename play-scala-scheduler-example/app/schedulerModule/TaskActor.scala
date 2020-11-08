package schedulerModule



import javax.inject.{Inject, Singleton}
import akka.actor.{Actor, Props}



import scala.concurrent.ExecutionContext

@Singleton
class TaskActor @Inject()()(implicit ec: ExecutionContext) extends Actor {
  import  TaskActor._

  /**
   * This method receives messages for an actor. Right now we are passing a string nothing from
   * TaskScheduler to it. You can inject your services in this module and call their method inside
   * the case block. And you have to inject the same in Task Scheduler and pass their reference
   * while creating the actor.*/
  override def receive: Receive = {
    case _ => {
      println("=================")
      println("scheduler invoked")
    }
  }
}

object TaskActor{
  def props: Props = Props[TaskActor]
}



