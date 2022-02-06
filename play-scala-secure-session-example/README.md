# play-scala-secure-session-example

This is an example application that shows how to do simple secure session management in Play, using the Scala API and session cookies.

## Overview

Play has a simple session cookie that is signed, but not encrypted.  This example shows how to securely store information in a client side cookie without revealing it to the browser, by encrypting the data with libsodium, a high level encryption library.

The only server side state is a mapping of session ids to secret keys.  When the user logs out, the mapping is deleted, and the encrypted information cannot be retrieved using the client's session id.  This prevents replay attacks after logout, even if the user saves off the cookies and replays them with exactly the same browser and IP address.

## Prerequisites

As with all Play projects, you must have JDK 1.8 and [sbt](http://www.scala-sbt.org/) installed.

However, you must install libsodium before using this application, which is a non-Java binary install.

If you are on MacOS, you can use Homebrew:

```bash
brew install libsodium
```

If you are on Ubuntu >= 15.04 or Debian >= 8, you can install with apt-get:

```bash
apt-get install libsodium-dev
```

On Fedora:

```bash
dnf install libsodium-devel
```

On CentOS:

```bash
yum install libsodium-devel
```

For Windows, you can download pre-built libraries using the [install page](https://download.libsodium.org/doc/installation/).

## Running

Run sbt from the command line:

```bash
sbt run
```

Then go to <http://localhost:9000> to see the server.

## Encryption

Encryption is handled by `services.encryption.EncryptionService`.  It uses secret key authenticated encryption with [Kalium](https://github.com/abstractj/kalium/), a thin Java wrapper around libsodium.  Kalium's `SecretBox` is an object oriented mapping to libsodium's `crypto_secretbox_easy` and `crypto_secretbox_open_easy`, described [here](https://download.libsodium.org/doc/secret-key_cryptography/authenticated_encryption.html).  The underlying stream cipher is XSalsa20, used with a Poly1305 MAC.

A abstract [cookie baker](https://www.playframework.com/documentation/latest/api/scala/index.html#play.api.mvc.CookieBaker), `EncryptedCookieBaker` is used to serialize and deserialize encrypted text between a `Map[String, String]` and a case class representation.  `EncryptedCookieBaker` also extends the `JWTCookieDataCodec` trait, which handles the encoding between `Map[String, String]` and the raw string data written out in the HTTP response in [JWT format](https://tools.ietf.org/html/rfc7519).

A factory `UserInfoCookieBakerFactory` creates a `UserInfoCookieBaker` that uses the session specific secret key to map a `UserInfo` case class to and from a cookie.

Then finally, a `UserInfoAction`, an action builder, handles the work of reading in a `UserInfo` from a cookie and attaches it to a `UserRequest`, a [wrapped request](https://www.playframework.com/documentation/latest/ScalaActionsComposition) so that the controllers can work with `UserInfo` without involving themselves with the underlying logic.

## Replicated Caching

In a production environment, there will be more than one Play instance.  This means that the session id to secret key to secret key mapping must be available to all the play instances, and when the session is deleted, the secret key must be removed from all the instances immediately.

This example uses `services.session.SessionService` to provide a `Future` based API around a session store.

### Distributed Data Session Store

The example internally uses [Akka Distributed Data](http://doc.akka.io/docs/akka/current/scala/distributed-data.html) to share the map throughout all the Play instances through [Akka Clustering](http://doc.akka.io/docs/akka/current/scala/cluster-usage.html).  Per the Akka docs, this is a good solution for up to 100,000 concurrent sessions.

The basic structure of the cache is taken from [Akka's ReplicatedCache example](https://github.com/akka/akka-samples/blob/HEAD/akka-sample-distributed-data-scala/src/main/scala/sample/distributeddata/ReplicatedCache.scala), but here an expiration time is added to ensure that an idle session will be reaped after reaching TTL, even if there is no explicit logout.  This does result in an individual actor per session, but the ActorCell only becomes active when there is a change in session state, so this is very low overhead.

Since this is an example, rather than having to run several Play instances, a ClusterSystem that runs two Akka cluster nodes in the background is used, and are configured as the seed nodes for the cluster, so you can see the cluster messages in the logs.  In production, each Play instance should be part of the cluster and they will take care of themselves.

> Note that the map is not persisted in this example, so **if all the Play instances go down at once, then everyone is logged out.**
>
> Also note that this uses Artery, which uses UDP without transport layer encryption.  **It is assumed transport level encryption is handled by the datacenter.**

### Database Session Store

If the example's CRDT implementation is not sufficient, you can use a regular database as a session store. Redis, Cassandra, or even an SQL database are all fine -- SQL databases are [extremely fast](https://thebuild.com/blog/2015/10/30/dont-assume-postgresql-is-slow/) at retrieving simple values.
