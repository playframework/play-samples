# play-scala-secure-session-example

This is an example application that shows how to use symmetric encryption with [Kalium](https://github.com/abstractj/kalium/) to do simple secure session management in Play, using the Scala API and session cookies.

## Prerequisites

You must install libsodium before using this application.  If you have homebrew, you can use `brew install libsodium`.

## Overview

Play has a simple session cookie that is signed, but not encrypted.  This example shows how to securely store information in a client side cookie without revealing it to the browser, by encrypting the data with libsodium, a high level encryption library.

The only server side state is a mapping of session ids to secret keys.  When the user logs out, the mapping is deleted, and the encrypted information cannot be retrieved using the client's session id.  This prevents replay attacks after logout, even if the user saves off the cookies and replays them with exactly the same browser and IP address.
