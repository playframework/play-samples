# Play File Upload using a custom BodyParser

This is a sample project that shows how to upload a file through Akka Streams using a custom BodyParser using Akka Streams using the Scala API.

Play's Scala API for `parse.multipartFormData` uses a `BodyParser[MultipartFormData[TemporaryFile]]`.  The `TemporaryFile` wrapper class creates a file under a "temporary" name and then deletes it only when the system is under GC pressure.

## Customizing the Body Parser

There are cases where it's useful to have more control over where and Play uploads multi part form data.  In this case, we'd like to get access to the accumulated byte stream for each file part and generate a file directly, without going through `TemporaryFile`.

In short, we want to replace:

```scala
Action(parse.multipartFormData)
```

with

```scala
Action(parse.multipartFormData(handleFilePartAsFile))
```

And we want to change as little code as possible.  The underlying mechanics are simple -- rather than use the default parser, a method `handleFilePartAsFile` is called in the action and returns a file:

``` scala
def upload = Action(parse.multipartFormData(handleFilePartAsFile)) { implicit request =>
  val fileOption = request.body.file("name").map {
    case FilePart(key, filename, contentType, file) =>
      logger.info(s"key = ${key}, filename = ${filename}, contentType = ${contentType}, file = $file")
      val data = operateOnTempFile(file)
      data
  }

  Ok(s"file size = ${fileOption}")
}
```

The implementation of `handleFilePartAsFile` uses a type alias `FilePartHandler` that is returned, and a custom accumulator will pull a file from anywhere on the filesystem (here we are using `Files.createTempFile`)

```scala
type FilePartHandler[A] = FileInfo => Accumulator[ByteString, FilePart[A]]

private def handleFilePartAsFile: FilePartHandler[File] = {
  case FileInfo(partName, filename, contentType) =>
    val attr = PosixFilePermissions.asFileAttribute(util.EnumSet.of(OWNER_READ, OWNER_WRITE))
    val path: Path = Files.createTempFile("multipartBody", "tempFile", attr)
    val file = path.toFile
    val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toFile(file)
    val accumulator: Accumulator[ByteString, IOResult] = Accumulator(fileSink)
    accumulator.map {
      case IOResult(count, status) =>
        logger.info(s"count = $count, status = $status")
        FilePart(partName, filename, contentType, file)
    }(play.api.libs.concurrent.Execution.defaultContext)
}
```
