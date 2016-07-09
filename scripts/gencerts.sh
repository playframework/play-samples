#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export PW=`pwgen -Bs 10 1`
echo ${PW} > ${DIR}/password

${DIR}/genca.sh
${DIR}/genclient.sh

${DIR}/gen-example.com.sh
${DIR}/gen-one.example.com.sh
${DIR}/gen-two.example.com.sh
${DIR}/gen-wildcard.example.com.sh

${DIR}/gentrustanchor.sh
