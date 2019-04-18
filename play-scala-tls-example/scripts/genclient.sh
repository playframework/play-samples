#!/bin/bash

export PW=`cat password`

# Create a self signed certificate & private key to create a root certificate authority.
keytool -genkeypair -v \
  -alias clientca \
  -keystore client.jks \
  -dname "CN=clientca, OU=Example Org, O=Example Company, L=San Francisco, ST=California, C=US" \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg EC \
  -keysize 256 \
  -ext KeyUsage:critical="keyCertSign" \
  -ext BasicConstraints:critical="ca:true" \
  -validity 365

# Create another key pair that will act as the client.  We want this signed by the client CA.
keytool -genkeypair -v \
  -alias client \
  -keystore client.jks \
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
  -keystore client.jks \
  -file client.csr

# Make clientCA create a certificate chain saying that client is signed by clientCA.
keytool -gencert -v \
  -alias clientca \
  -keypass:env PW \
  -storepass:env PW \
  -keystore client.jks \
  -infile client.csr \
  -outfile client.crt \
  -ext EKU="clientAuth" \
  -rfc

# Export the client-ca certificate from the keystore.  This goes to nginx under "ssl_client_certificate"
# and is presented in the CertificateRequest.
keytool -export -v \
  -alias clientca \
  -file clientca.crt \
  -storepass:env PW \
  -keystore client.jks \
  -rfc

# Import the signed client certificate back into client.jks.  This is important, as JSSE won't send a client
# certificate if it can't find one signed by the client-ca presented in the CertificateRequest.
keytool -import -v \
  -alias client \
  -file client.crt \
  -keystore client.jks \
  -storetype JKS \
  -storepass:env PW

# Export the client CA to pkcs12, so it's safe. 
keytool -importkeystore -v \
  -srcalias clientca \
  -srckeystore client.jks \
  -srcstorepass:env PW \
  -destkeystore client.p12 \
  -deststorepass:env PW \
  -deststoretype PKCS12

# Import the client CA's public certificate into a JKS store for Play Server to read (we don't use
# the PKCS12 because it's got the CA private key and we don't want that.
keytool -import -v \
  -alias clientca \
  -file clientca.crt \
  -keystore clientca.jks \
  -storepass:env PW << EOF
yes
EOF

# List out the contents of client.jks just to confirm it.
keytool -list -v \
  -keystore client.jks \
  -storepass:env PW
