package controllers

import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import play.api.inject._

/**
 * To provide sanitization for chat messages.
 */
trait InputSanitizer {
  def sanitize(input: String): String
}

class JSoupInputSanitizer extends InputSanitizer {
  override def sanitize(input: String): String = {
    Jsoup.clean(input, Whitelist.basic())
  }
}

class InputSanitizerModule extends SimpleModule(
  bind[InputSanitizer].to[JSoupInputSanitizer]
)
