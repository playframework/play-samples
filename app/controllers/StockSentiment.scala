package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{AnyContent, Controller, Action, Result}
import play.api.libs.ws.{Response, WS}
import scala.concurrent.Future
import play.api.libs.json.{JsString, Json, JsValue}
import play.api.Play

object StockSentiment extends Controller {

  case class Tweet(text: String)
  
  implicit val tweetReads = Json.reads[Tweet]
  
  def getTextSentiment(text: String): Future[Response] =
    WS.url(Play.current.configuration.getString("sentiment.url").get) post Map("text" -> Seq(text))

  def getAverageSentiment(responses: Seq[Response], label: String): Double = responses.map { response =>
    (response.json \\ label).head.as[Double]
  }.sum / responses.length.max(1) // avoid division by zero

  def loadSentimentFromTweets(json: JsValue): Seq[Future[Response]] =
    (json \ "statuses").as[Seq[Tweet]] map (tweet => getTextSentiment(tweet.text))

  def getTweets(symbol:String): Future[Response] = {
    WS.url(Play.current.configuration.getString("tweet.url").get.format(symbol)).get.withFilter { response =>
      response.status == OK
    }
  }
  
  def get(symbol: String): Action[AnyContent] = Action {
    Async {
      val futureStockSentiments: Future[Result] = for {
        tweets <- getTweets(symbol) // get tweets that contain the stock symbol
        futureSentiments = loadSentimentFromTweets(tweets.json) // queue web requests each tweets' sentiments
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
      futureStockSentiments.recoverWith {
        case nsee: NoSuchElementException =>
          Future(InternalServerError(Json.obj("error" -> JsString("Could not fetch the tweets"))))
      }
      futureStockSentiments
    }
  }
}