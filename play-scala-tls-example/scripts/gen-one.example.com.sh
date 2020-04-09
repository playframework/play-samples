#!/bin/bash

export PW=`cat password`

# Create a server certificate, tied to one.example.com
# Uses a 10 year validity to simplify maintenance. Consider what validity is more convenient for your use case
keytool -genkeypair -v \
  -alias one.example.com \
  -dname "CN=one.example.com, OU=Example Org, O=Example Company, L=San Francisco, ST=California, C=US" \
  -keystore example.com.p12 \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg EC \
  -keysize 256 \
  -validity 3650

# Create a certificate signing request for one.example.com
keytool -certreq -v \
  -alias one.example.com \
  -keypass:env PW \
  -storepass:env PW \
  -keystore example.com.p12 \
  -file one.example.com.csr

# Tell exampleCA to sign the example.com certificate.
# Technically, digitalSignature for DHE or ECDHE, keyEncipherment for RSA
keytool -gencert -v \
  -alias exampleca \
  -keypass:env PW \
  -storepass:env PW \
  -keystore exampleca.p12 \
  -infile one.example.com.csr \
  -outfile one.example.com.crt \
  -ext KeyUsage:critical="digitalSignature,keyEncipherment" \
  -ext EKU="serverAuth" \
  -ext SAN="DNS:one.example.com" \
  -rfc \
  -validity 3650


# Import the signed certificate back into example.com.p12
keytool -import -v \
  -alias one.example.com \
  -file one.example.com.crt \
  -keystore example.com.p12 \
  -storetype PKCS12 \
  -storepass:env PW

# List out the contents of example.com.p12 just to confirm it.
# If you are using Play as a TLS termination point, this is the key store you should use.
#keytool -list -v \
#  -keystore example.com.p12 \
#  -storepass:env PW
