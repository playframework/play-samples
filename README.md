## Play File Upload using a custom BodyParser

This is a sample project that shows how to upload a file through Akka Streams using a custom BodyParser.

If you want to use `multipart/form-data` encoding, you can still use the default `mutipartFormData` parser by providing your own `PartHandler[FilePart[A]]`. You receive the part headers, and you have to provide an `Iteratee[Array[Byte], FilePart[A]]` that will produce the right `FilePart`.

