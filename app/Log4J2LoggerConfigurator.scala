import java.io.File
import java.net.URL

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.Configurator
import play.api.{Environment, LoggerConfigurator, Mode}

class Log4J2LoggerConfigurator extends LoggerConfigurator {

  override def init(rootPath: File, mode: Mode.Mode): Unit = {
    val properties = Map("application.home" -> rootPath.getAbsolutePath)
    val resourceName = if (mode == Mode.Dev) "log4j2-dev.xml" else "log4j2.xml"
    val resourceUrl = Option(this.getClass.getClassLoader.getResource(resourceName))
    configure(properties, resourceUrl)
  }

  override def shutdown(): Unit = {
    val context = LogManager.getContext().asInstanceOf[LoggerContext]
    Configurator.shutdown(context)
  }

  override def configure(env: Environment): Unit = {
    val properties = Map("application.home" -> env.rootPath.getAbsolutePath)
    val resourceUrl = env.resource("log4j2.xml")
    configure(properties, resourceUrl)
  }

  override def configure(properties: Map[String, String], config: Option[URL]): Unit = {
    val context = LogManager.getContext(false).asInstanceOf[LoggerContext]
    context.setConfigLocation(config.get.toURI)
  }
}
