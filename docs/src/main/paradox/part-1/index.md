# Basics

This guide will walk you through how to make a REST API with JSON using [Play 2.5](https://playframework.com).

To see the associated Github project, please go to [https://github.com/playframework/play-rest-api](https://github.com/playframework/play-rest-api) or clone the project:

```
git clone https://github.com/playframework/play-rest-api.git
```

We're going to be showing an already working Play project with most of the code available under the "app/v1" directory.  There will be several different versions of the same project as this series expands, so you can compare different versions of the project against each other.

To run Play on your own local computer, please see the instructions in the [appendix](../appendix.html). 

## Introduction

We'll start off with a REST API that displays information for blog posts.  Users should be able to write a title and a body of a blog post and create new blog posts, edit existing blog posts, and delete new blog posts.

## Modelling a Post Resource

The way to do this in REST is to model the represented state as a resource.  A blog post resource will have a unique id, a URL hyperlink that indicates the canonical location of the resource, the title of the blog post, and the body of the blog post.

This resource is represented as a single case class in the Play application [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostResourceHandler.scala#L13):

```scala
case class PostResource(id: String, link: String,
                        title: String, body: String)
```

This resource is mapped to and from JSON on the front end using Play, and is mapped to and from a persistent datastore on the backend using a handler.  

Play handles HTTP routing and representation for the REST API and makes it easy to write a non-blocking, asynchronous API that is an order of magnitude more efficient than other web application frameworks.

## Routing Post Requests

Play has two complimentary routing mechanisms.  In the conf directory, there's a file called "routes" which contains entries for the HTTP method and a relative URL path, and points it at an action in a controller.

```
GET    /               controllers.HomeController.index()
```

This is useful for situations where a front end service is rendering HTML.  However, Play also contains a more powerful routing DSL that we will use for the REST API.

For every HTTP request starting with `/v1/posts`, Play routes it to a dedicated `PostRouter` class to handle the Posts resource, through the [`conf/routes`](https://github.com/playframework/play-rest-api/blob/master/conf/routes) file:

```
->     /v1/posts               v1.post.PostRouter
```

The `PostRouter` examines the URL and extracts data to pass along to the controller [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostRouter.scala):

```scala
package v1.post
import javax.inject.Inject

import play.api.mvc._
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class PostRouter @Inject()(controller: PostController)
  extends SimpleRouter
{
  override def routes: Routes = {
    case GET(p"/") =>
     controller.index
      
    case POST(p"/") =>
      controller.process

    case GET(p"/$id") =>
      controller.show(id)
  }
}
```

Play’s [routing DSL](https://www.playframework.com/documentation/2.5.x/ScalaSirdRouter) (technically "String Interpolation Routing DSL", aka SIRD) shows how data can be extracted from the URL concisely and cleanly.  SIRD is based around HTTP methods and a string interpolated extractor object – this means that when we type the string “/$id” and prefix it with “p”, then the path parameter id can be extracted and used in the block. Naturally, there are also operators to extract queries, regular expressions, and even add custom extractors.  If you have a URL as follows:

```
/posts/?sort=ascending&count=5
```

then you can extract the "sort" and "count" parameters in a single line:

```scala
GET("/" ? q_?"sort=$sort" & q_?”count=${ int(count) }")
```

SIRD is especially useful in a REST API where there can be many possible query parameters. Cake Solutions covers SIRD in more depth in a [fantastic blog post](http://www.cakesolutions.net/teamblogs/all-you-need-to-know-about-plays-routing-dsl).

## Using a Controller

The PostRouter has a PostController injected into it through standard [JSR-330 dependency injection](https://github.com/google/guice/wiki/JSR330) [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostRouter.scala#L12):

```scala
class PostRouter @Inject()(controller: PostController)
  extends SimpleRouter
```

Before heading into the PostController, let's discuss how controllers work in Play.

A controller [handles the work of processing](https://www.playframework.com/documentation/2.5.x/ScalaActions)  the HTTP request into an HTTP response in the context of an Action: it's where page rendering and HTML form processing happen.  A controller extends [`play.api.mvc.Controller`](https://playframework.com/documentation/2.5.x/api/scala/index.html#play.api.mvc.Controller), which contains a number of utility methods and constants for working with HTTP.  In particular, a Controller contains Result objects such as Ok and Redirect, and HeaderNames like ACCEPT.

The methods in a controller consist of a method returning an [Action](https://playframework.com/documentation/2.5.x/api/scala/index.html#play.api.mvc.Action).  The Action provides the "engine" to Play.

Using the action, the controller passes in a block of code that takes a [`Request`](https://playframework.com/documentation/2.5.x/api/scala/index.html#play.api.mvc.Request) passed in as implicit – this means that any in-scope method that takes an implicit request as a parameter will use this request automatically.  Then, the block must return either a [`Result`](https://playframework.com/documentation/2.5.x/api/scala/index.html#play.api.mvc.Result), or a [`Future[Result]`](http://www.scala-lang.org/api/current/index.html#scala.concurrent.Future), depending on whether or not the action was called as `action { ... }` or [`action.async { ... }`](https://www.playframework.com/documentation/2.5.x/ScalaAsync#How-to-create-a-Future[Result]). 
 
### Handling GET Requests


Here's a simple example of a Controller:
 
```scala
import javax.inject.Inject
import play.api.mvc._

import scala.concurrent._

class MyController extends Controller {

  def index1: Action[AnyContent] = {
    Action { implicit request =>
      val r: Result = Ok("hello world")
      r
    }
  }

  def asyncIndex: Action[AnyContent] = {
    Action.async { implicit request =>
      val r: Future[Result] = Future.successful(Ok("hello world"))
      r
    }
  }
}
```

In this example, `index1` and `asyncIndex` have exactly the same behavior.  Internally, it makes no difference whether we call `Result` or `Future[Result]` -- Play is non-blocking all the way through. 

However, if you're already working with `Future`, async makes it easier to pass that `Future` around. You can read more about this in the [handling asynchronous results](https://www.playframework.com/documentation/2.5.x/ScalaAsync) section of the Play documentation.

The PostController methods dealing with GET requests is [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostController.scala):

```scala
class PostController @Inject()(action: PostAction,
                              handler: PostResourceHandler)
                             (implicit ec: ExecutionContext)
 extends Controller {

 def index: Action[AnyContent] = {
   action.async { implicit request =>
     handler.find.map { posts =>
       Ok(Json.toJson(posts))
     }
   }
 }

 def show(id: String): Action[AnyContent] = {
   action.async { implicit request =>
     handler.lookup(id).map { post =>
       Ok(Json.toJson(post))
     }
   }
 }

}
```

Let's take `show` as an example.  Here, the action defines a workflow for a request that maps to a single resource, i.e. `GET /v1/posts/123`.  

```scala
def show(id: String): Action[AnyContent] = {
  action.async { implicit request =>
    handler.lookup(id).map { post =>
      Ok(Json.toJson(post))
    }
  }
}
```

The id is passed in as a String, and the handler looks up and returns a `PostResource`.  The `Ok()` sends back a `Result` with a status code of "200 OK", containing a response body consisting of the `PostResource` serialized as JSON.

### Processing Form Input

Handling a POST request is also easy and is done through the `process` method:

```scala
class PostController @Inject()(action: PostAction,
                              handler: PostResourceHandler)
                             (implicit ec: ExecutionContext)
  extends Controller {

  private val form: Form[PostFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "title" -> nonEmptyText,
        "body" -> text
      )(PostFormInput.apply)(PostFormInput.unapply)
    )
  }

  def process: Action[AnyContent] = {
    action.async { implicit request =>
      processJsonPost()
    }
  }

  private def processJsonPost[A]()(implicit request: PostRequest[A]):  Future[Result] = {
    def failure(badForm: Form[PostFormInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: PostFormInput) = {
      handler.create(input).map { post =>
        Created(Json.toJson(post))
          .withHeaders(LOCATION -> post.link)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
```

Here, the `process` action is an action wrapper, and `processJsonPost` does most of the work.  In `processJsonPost`, we get to the [form processing](https://www.playframework.com/documentation/2.5.x/ScalaForms) part of the code.  

Here, `form.bindFromRequest()` will map input from the HTTP request to a [`play.api.data.Form`](https://www.playframework.com/documentation/2.5.x/api/scala/index.html#play.api.data.Form), and handles form validation and error reporting.  

If the `PostFormInput` passes validation, it's passed to the resource handler, using the `success` method.  If the form processing fails, then the `failure` method is called and the `FormError` is returned in JSON format.

```scala
private val form: Form[PostFormInput] = {
  import play.api.data.Forms._

  Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> text
    )(PostFormInput.apply)(PostFormInput.unapply)
  )
}
```

The form binds to the HTTP request using the names in the mapping -- "title" and "body" to the `PostFormInput` case class [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostController.scala#L11).

```scala
case class PostFormInput(title: String, body: String)
```

That's all you need to do to handle a basic web application!  As with most things, there are more details that need to be handled.  That's where creating custom Actions comes in.

## Using Actions

We saw in the `PostController` that each method is connected to an Action through the "action.async" method [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostController.scala#L32):

```scala
  def index: Action[AnyContent] = {
    action.async { implicit request =>
      handler.find.map { posts =>
        Ok(Json.toJson(posts))
      }
    }
  }
```

The action.async takes a function, and comes from the class parameter "action", which we can see is of type `PostAction` [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostController.scala#L16):

```scala
class PostController @Inject()(action: PostAction [...])
```

`PostAction` is an ActionBuilder.  It is involved in each action in the controller -- it mediates the paperwork involved with processing a request into a response, adding context to the request and enriching the response with headers and cookies.  ActionBuilders are essential for handling authentication, authorization and monitoring functionality.

ActionBuilders work through a process called [action composition](https://www.playframework.com/documentation/2.5.x/ScalaActionsComposition).  The ActionBuilder class has a method called `invokeBlock` that takes in a `Request` and a function (also known as a block, lambda or closure) that accepts a `Request` of a given type, and produces a `Future[Result]`.

So, if you want to work with an `Action` that has a "FooRequest" that has a Foo attached, it's easy: 

```scala
class FooRequest[A](request: Request[A], val foo: Foo) extends WrappedRequest(request)

class FooAction extends ActionBuilder[FooRequest] {
  type FooRequestBlock[A] = FooRequest[A] => Future[Result]

  override def invokeBlock[A](request: Request[A], block: FooRequestBlock[A]) = {
    block(new FooRequest[A](request, new Foo))
  }
}
```

You create an `ActionBuilder[FooRequest]`, override `invokeBlock`, and then call the function with an instance of `FooRequest`.  

Then, when you call `fooAction`, the request type is `FooRequest`:

```scala
fooAction { request: FooRequest => 
  Ok(request.foo.toString)
}
```

And `request.foo` will be added automatically.

You can keep composing action builders inside each other, so you don't have to layer all the functionality in one single ActionBuilder, or you can create a custom `ActionBuilder` for each package you work with, according to your taste.  For the purposes of this blog post, we'll keep everything together in a single class.

You can see PostAction [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostAction.scala):

```scala
class PostRequest[A](request: Request[A], 
                     val messages: Messages)
  extends WrappedRequest(request)

class PostAction @Inject()(messagesApi: MessagesApi)
                          (implicit ec: ExecutionContext)
  extends ActionBuilder[PostRequest] with HttpVerbs {

  type PostRequestBlock[A] = PostRequest[A] => Future[Result]

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  override def invokeBlock[A](request: Request[A], 
                              block: PostRequestBlock[A]) = {
    if (logger.isTraceEnabled()) {
      logger.trace(s"invokeBlock: request = $request")
    }

    val messages = messagesApi.preferred(request)
    val future = block(new PostRequest(request, messages))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }
  }
}
```

`PostAction` does a couple of different things here.  The first thing it does is to log the request as it comes in.  Next, it pulls out the localized `Messages` for the request, and adds that to a `PostRequest` , and runs the function, returning a `Future[Result]`.

When the future completes, we map the result so we can replace it with a slightly different result.  We compare the result's method against `HttpVerbs`, and if it's a GET or HEAD, we append a Cache-Control header with a max-age directive.  We need an `ExecutionContext` for `future.map` operations, so we pass in the default execution context implicitly at the top of the class.

Now that we have a `PostRequest`, we can call "request.messages" explicitly from any action in the controller, for free, and we can append information to the result after the user action has been completed.

## Converting resources with PostResourceHandler

The `PostResourceHandler` is responsible for converting backend data from a repository into a `PostResource`. We won't go into detail on the `PostRepository` details for now, only that it returns data in an backend-centric state.

A REST resource has information that a backend repository does not -- it knows about the operations available on the resource, and contains URI information that a single backend may not have.  As such, we want to be able to change the representation that we use internally without changing the resource that we expose publicly.  

You can see the `PostResourceHandler` [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostResourceHandler.scala):

```scala
class PostResourceHandler @Inject()(routerProvider: Provider[PostRouter],
                                   postRepository: PostRepository)
                                  (implicit ec: ExecutionContext)
{

 def create(postInput: PostFormInput): Future[PostResource] = {
   val data = PostData(PostId("999"), postInput.title, postInput.body)
   postRepository.create(data).map { id =>
     createPostResource(data)
   }
 }

 def lookup(id: String): Future[Option[PostResource]] = {
   val postFuture = postRepository.get(PostId(id))
   postFuture.map { maybePostData =>
     maybePostData.map { postData =>
       createPostResource(postData)
     }
   }
 }

 def find: Future[Iterable[PostResource]] = {
   postRepository.list().map { postDataList =>
     postDataList.map(postData => createPostResource(postData))
   }
 }

 private def createPostResource(p: PostData): PostResource = {
   PostResource(p.id.toString, routerProvider.get.link(p.id), p.title, p.body)
 }

}
```

Here, it's a straight conversion in `createPostResource`, with the only hook being that the router provides the resource's URL, since it's something that `PostData` doesn't have itself.

## Rendering Content as JSON

Play handles the work of converting a `PostResource` through [Play JSON](https://www.playframework.com/documentation/2.5.x/ScalaJson). Play JSON provides a DSL that looks up the conversion for the `PostResource` singleton object, so you don't need to declare it at the use point.

You can see the `PostResource` object [here](https://github.com/playframework/play-rest-api/blob/master/app/v1/post/PostResourceHandler.scala#L18):

```scala
object PostResource {
  implicit val implicitWrites = new Writes[PostResource] {
    def writes(post: PostResource): JsValue = {
      Json.obj(
        "id" -> post.id,
        "link" -> post.link,
        "title" -> post.title,
        "body" -> post.body)
    }
  }
}
```

Once the implicit is defined in the companion object, then it will be looked up automatically when handed an instance of the class.  This means that when the controller converts to JSON, the conversion will just work, without any additional imports or setup.  

```scala
val json: JsValue = Json.toJson(post)
```

Play JSON also has options to incrementally parse and generate JSON for continuously streaming JSON responses.

## Summary

We've shown how to easy it is to put together a basic REST API in Play.  Using this code, we can put together backend data, convert it to JSON and transfer it over HTTP with a minimum of fuss.

In the next guide, we'll discuss content representation and provide an HTML interface that exists alongside the JSON API.
