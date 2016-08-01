package com.lightbend.blog.comment

final case class CommentData(id: CommentId, postId: String, body: String)

class CommentId private(val underlying: String) extends AnyVal {
  override def toString: String = underlying.toString
}

object CommentId {
  def apply(raw: String): CommentId = new CommentId(raw)
}
