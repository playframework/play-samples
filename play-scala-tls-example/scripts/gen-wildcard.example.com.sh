#!/bin/bash

export PW=`cat password`

# Create a server certificate, tied to *.example.com
# Uses a 10 year validity to simplify maintenance. Consider what validity is more convenient for your use case
keytool -genkeypair -v \
  -alias wildcard.example.com \
  -dname "CN=*.example.com, OU=Example Org, O=Example Company, L=San Francisco, ST=California, C=US" \
  -keystore example.com.p12 \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg EC \
  -keysize 256 \
  -validity 3650

# Create a certificate signing request for example.com
keytool -certreq -v \
  -alias wildcard.example.com \
  -keypass:env PW \
  -storepass:env PW \
  -keystore example.com.p12 \
  -file wildcard.example.com.csr

# Tell exampleCA to sign the example.com certificate. 
# Technically, digitalSignature for DHE or ECDHE, keyEncipherment for RSA
# You'd think you could use SAN here, but https://stackoverflow.com/questions/33827789/self-signed-certificate-dnsname-components-must-begin-with-a-letter
keytool -gencert -v \
  -alias exampleca \
  -keypass:env PW \
  -storepass:env PW \
  -keystore exampleca.p12 \
  -infile wildcard.example.com.csr \
  -outfile wildcard.example.com.crt \
  -ext KeyUsage:critical="digitalSignature,keyEncipherment" \
  -ext EKU="serverAuth" \
  -rfc \
  -validity 3650

# Import the signed certificate back into example.com.p12 
keytool -import -v \
  -alias wildcard.example.com \
  -file wildcard.example.com.crt \
  -keystore example.com.p12 \
  -storetype PKCS12 \
  -storepass:env PW

# List out the contents of example.com.p12 just to confirm it.  
# If you are using Play as a TLS termination point, this is the key store you should use.
#keytool -list -v \
#  -keystore example.com.p12 \
#  -storepass:env PW
