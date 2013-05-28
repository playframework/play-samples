package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{AnyContent, Controller, Action}
import play.api.libs.ws.{Response, WS}
import utils.Global
import scala.concurrent.Future
import play.api.libs.json.{JsString, Json, JsValue}

object StockSentiment extends Controller {

  def getTextSentiment(text: String): Future[Response] = WS.url(Global.sentimentUrl) post Map("text" -> Seq(text))

  def getAverageSentiment(responses: Seq[Response], label: String): Double = responses.map { response =>
    (response.json \\ label).head.as[Double]
  }.sum / responses.length.max(1) // avoid division by zero

  def loadSentimentFromTweet(json: JsValue): Seq[Future[Response]] = (json \\ "text") map (_.as[String]) map getTextSentiment

  def get(symbol: String): Action[AnyContent] = Action {
    Async {
      for {
        tweets <- WS.url(Global.tweetUrl.format(symbol)).get // get tweets that contain the stock symbol
        futureSentiments = loadSentimentFromTweet(tweets.json) // queue web requests to get the sentiments of each tweet
        sentiments <- Future.sequence(futureSentiments) // when the sentiment responses arrive, set them
      } yield {
        def averageSentiment(label: String) = getAverageSentiment(sentiments, label)
        val neg = averageSentiment("neg")
        val neutral = averageSentiment("neutral")
        val pos = averageSentiment("pos")

        val response = Json.obj(
          "probability" -> Json.obj(
            "neg" -> neg,
            "neutral" -> neutral,
            "pos" -> pos
          )
        )
        val classification =
          if (neutral > 0.5)
            "neutral"
          else if (neg > pos)
            "neg"
          else
            "pos"

        Ok(response + ("label" -> JsString(classification)))
      }
    }
  }
}