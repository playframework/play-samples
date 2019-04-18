package controllers

import javax.inject._

import play.api.{Configuration, Logger}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class StockSentiment @Inject()(ws: WSClient,
                               configuration: Configuration,
                               cc: ControllerComponents)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  private val logger = Logger(this.getClass)

  private val sentimentUrl = configuration.get[String]("sentiment.url")

  private val tweetUrl = configuration.get[String]("tweet.url")

  case class Tweet(text: String)
  
  private implicit val tweetReads = Json.reads[Tweet]

  def get(symbol: String): Action[AnyContent] = Action.async {
    logger.info(s"getting stock sentiment for $symbol")

    val futureStockSentiments: Future[Result] = for {
      tweets <- getTweets(symbol) // get tweets that contain the stock symbol
      futureSentiments = loadSentimentFromTweets(tweets.json) // queue web requests each tweets' sentiments
      sentiments <- Future.sequence(futureSentiments) // when the sentiment responses arrive, set them
    } yield Ok(sentimentJson(sentiments))

    futureStockSentiments.recover {
      case nsee: NoSuchElementException =>
        InternalServerError(Json.obj("error" -> JsString("Could not fetch the tweets")))
    }
  }

  private def getTextSentiment(text: String): Future[WSResponse] = {
    logger.info(s"getTextSentiment: text = $text")

    ws.url(sentimentUrl).post(Map("text" -> Seq(text)))
  }

  private def getAverageSentiment(responses: Seq[WSResponse], label: String): Double = {
    responses.map { response =>
      (response.json \\ label).head.as[Double]
    }.sum / responses.length.max(1)
  } // avoid division by zero

  private def loadSentimentFromTweets(json: JsValue): Seq[Future[WSResponse]] = {
    (json \ "statuses").as[Seq[Tweet]] map (tweet => getTextSentiment(tweet.text))
  }

  private def getTweets(symbol:String): Future[WSResponse] = {
    logger.info(s"getTweets: symbol = $symbol")

    ws.url(tweetUrl.format(symbol)).get.withFilter { response =>
      response.status == OK
    }
  }

  private def sentimentJson(sentiments: Seq[WSResponse]): JsObject = {
    logger.info(s"sentimentJson: sentiments = $sentiments")

    val neg = getAverageSentiment(sentiments, "neg")
    val neutral = getAverageSentiment(sentiments, "neutral")
    val pos = getAverageSentiment(sentiments, "pos")

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

    val r = response + ("label" -> JsString(classification))
    logger.info(s"response = $r")

    r
  }

}
