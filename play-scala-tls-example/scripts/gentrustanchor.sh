#!/bin/bash

export PW=`cat password`

# Create a PKCS12 keystore that trusts the example CA, with the default password.  
# This is used by the client in the trustmanager section.
keytool -import -v \
  -alias exampleca \
  -file exampleca.crt \
  -keypass:env PW \
  -storepass changeit \
  -storetype PKCS12 \
  -keystore exampletrust.p12 << EOF
yes
EOF

# List out the details of the store password.
keytool -list -v \
  -keystore exampletrust.p12 \
  -storepass changeit