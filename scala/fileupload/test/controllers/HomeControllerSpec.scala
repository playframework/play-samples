package controllers

import java.io._
import java.nio.file.Files

import org.apache.pekko.stream.scaladsl._
import org.apache.pekko.util.ByteString
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import play.api.libs.ws.DefaultBodyReadables.readableAsString

class HomeControllerSpec extends PlaySpec with GuiceOneServerPerSuite with Injecting {

  "HomeController" must {
    "upload a file successfully" in {
      val tmpFile = java.io.File.createTempFile("prefix", "txt")
      tmpFile.deleteOnExit()
      val msg = "hello world"
      Files.write(tmpFile.toPath, msg.getBytes())

      val url = s"http://localhost:${port}/upload"
      val responseFuture = inject[WSClient].url(url).post(postSource(tmpFile))
      val response = await(responseFuture)
      response.status mustBe OK
      response.body mustBe "file size = 11"
    }
  }

  def postSource(tmpFile: File): Source[MultipartFormData.Part[Source[ByteString, _]], _] = {
    import play.api.mvc.MultipartFormData._
    Source(FilePart("name", "hello.txt", Option("text/plain"),
      FileIO.fromPath(tmpFile.toPath)) :: DataPart("key", "value") :: List())
  }
}
