#!/bin/bash

export PW=`cat password`

# Create a server certificate, tied to one.example.com
keytool -genkeypair -v \
  -alias one.example.com \
  -dname "CN=one.example.com, OU=Example Org, O=Example Company, L=San Francisco, ST=California, C=US" \
  -keystore example.com.jks \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg EC \
  -keysize 256 \
  -validity 385

# Create a certificate signing request for one.example.com
keytool -certreq -v \
  -alias one.example.com \
  -keypass:env PW \
  -storepass:env PW \
  -keystore example.com.jks \
  -file one.example.com.csr

# Tell exampleCA to sign the example.com certificate.
# Technically, digitalSignature for DHE or ECDHE, keyEncipherment for RSA
keytool -gencert -v \
  -alias exampleca \
  -keypass:env PW \
  -storepass:env PW \
  -keystore exampleca.jks \
  -infile one.example.com.csr \
  -outfile one.example.com.crt \
  -ext KeyUsage:critical="digitalSignature,keyEncipherment" \
  -ext EKU="serverAuth" \
  -ext SAN="DNS:one.example.com" \
  -rfc

# Import the signed certificate back into example.com.jks
keytool -import -v \
  -alias one.example.com \
  -file one.example.com.crt \
  -keystore example.com.jks \
  -storetype JKS \
  -storepass:env PW

# List out the contents of example.com.jks just to confirm it.
# If you are using Play as a TLS termination point, this is the key store you should use.
#keytool -list -v \
#  -keystore example.com.jks \
#  -storepass:env PW
