#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export PW=`pwgen -Bs 10 1`
echo ${PW} > ${DIR}/password

${DIR}/genca.sh
${DIR}/genclient.sh
${DIR}/genserver.sh
${DIR}/gentrustanchor.sh
