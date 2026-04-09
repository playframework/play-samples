package models

case class Cat(name: String, color: String)
object Cat {
  def unapply(cat: Cat): Option[(String, String)] = Some((cat.name, cat.color))
  def tupled: ((String, String)) => Cat = {
    case (name, color) => Cat(name, color)
  }
}

case class Dog(name: String, color: String)
object Dog {
  def unapply(dog: Dog): Option[(String, String)] = Some((dog.name, dog.color))
  def tupled: ((String, String)) => Dog = {
    case (name, color) => Dog(name, color)
  }
}
