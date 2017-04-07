# play-scala-kalium-example

This is an example application that shows how to use symmetric encryption with [Kalium](https://github.com/abstractj/kalium/) to do simple secure session management.

## Prerequisites

You must install libsodium before using this application.  If you have homebrew, you can use `brew install libsodium`.

## Overview

Play has a simple session cookie that is signed, but not encrypted.  This example shows how to securely store information in a client side cookie without revealing it to the browser, by encrypting the data with libsodium, a high level encryption library.

Sessions are managed by a key value store (here represented by Play Cache, but you would probably use Redis in production), and the only information kept on the server is the secret key used for encryption.  When the user logs out, the secret key is deleted, and the encrypted information cannot be retrieved.
