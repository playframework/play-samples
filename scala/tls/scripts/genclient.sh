#!/bin/bash

export PW=`cat password`

# Create a self signed certificate & private key to create a root certificate authority.
# Uses a 10 year validity to simplify maintenance. Consider what validity is more convenient for your use case
keytool -genkeypair -v \
  -alias clientca \
  -keystore client.p12 \
  -storetype PKCS12 \
  -dname "CN=clientca, OU=Example Org, O=Example Company, L=San Francisco, ST=California, C=US" \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg EC \
  -keysize 256 \
  -ext KeyUsage:critical="keyCertSign" \
  -ext BasicConstraints:critical="ca:true" \
  -validity 3650

# Create another key pair that will act as the client.  We want this signed by the client CA.
keytool -genkeypair -v \
  -alias client \
  -keystore client.p12 \
  -dname "CN=client, OU=Example Org, O=Example Company, L=San Francisco, ST=California, C=US" \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg EC \
  -keysize 256 \

# Create a certificate signing request from the client certificate.
keytool -certreq -v \
  -alias client \
  -keypass:env PW \
  -storepass:env PW \
  -keystore client.p12 \
  -file client.csr

# Make clientCA create a certificate chain saying that client is signed by clientCA.
keytool -gencert -v \
  -alias clientca \
  -keypass:env PW \
  -storepass:env PW \
  -keystore client.p12 \
  -infile client.csr \
  -outfile client.crt \
  -ext EKU="clientAuth" \
  -rfc \
  -validity 3650

# Export the client-ca certificate from the keystore.  This goes to nginx under "ssl_client_certificate"
# and is presented in the CertificateRequest.
keytool -export -v \
  -alias clientca \
  -file clientca.crt \
  -storepass:env PW \
  -keystore client.p12 \
  -rfc

# Import the signed client certificate back into client.p12.  This is important, as JSSE won't send a client
# certificate if it can't find one signed by the client-ca presented in the CertificateRequest.
keytool -import -v \
  -alias client \
  -file client.crt \
  -keystore client.p12 \
  -storetype PKCS12 \
  -storepass:env PW

# Import the client CA's public certificate into a PKCS12 store for Play Server to read (we don't use
# the PKCS12 because it's got the CA private key and we don't want that.
keytool -import -v \
  -alias clientca \
  -file clientca.crt \
  -keystore clientca.p12 \
  -storetype PKCS12 \
  -storepass:env PW << EOF
yes
EOF

# List out the contents of client.p12 just to confirm it.
keytool -list -v \
  -keystore client.p12 \
  -storepass:env PW
