package com.lightbend.blog.post

final case class PostData(id: PostId, title: String, body: String)

class PostId private(val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object PostId {
  def apply(raw: String): PostId = {
    require(raw != null)
    new PostId(Integer.parseInt(raw))
  }
}
