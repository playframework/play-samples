package schedulerModule

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}
import akka.actor.{ActorRef, ActorSystem, Props}
import javax.inject.{Inject, Singleton}


import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{DurationInt, FiniteDuration, MILLISECONDS}

@Singleton
class TaskScheduler @Inject()(val system: ActorSystem)(implicit ec: ExecutionContext) {

  val taskActor: ActorRef = system.actorOf(Props(new TaskActor()),"TaskActor")
  system.scheduler.scheduleWithFixedDelay(calculateInitialDelay(),5.seconds,taskActor,"nothing")
  //todo change delay to 24 hrs(24.hours) or whatever you need in production


  /**
   * This method calculates the initial delay between the time we set
   * and the present time. It's needed by scheduleWithFixedDelay method.*/
  def calculateInitialDelay():FiniteDuration = {
    val now = new Date()
    val sdf = new SimpleDateFormat("HH:mm:ss")
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
    //todo set timezone to where server is deployed


    val time1 = sdf.format(now)
    val time2 = "04:20:00"
    //todo change this time to 00:04:20 or whenever you want to start first time.

    val format = new SimpleDateFormat("HH:mm:ss")
    val date1 = format.parse(time1)
    val date2 = format.parse(time2)
    val timeDifference  = date2.getTime - date1.getTime
    new FiniteDuration(timeDifference,MILLISECONDS)

  }

}

