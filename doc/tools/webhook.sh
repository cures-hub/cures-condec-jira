#!/bin/bash

WEBHOOK_SECRET=$1
if [ -z "$WEBHOOK_SECRET" ]; then
	echo "Please provide the secret token"
	exit 1
fi

refname=123456

tmpfile=$(mktemp --suffix=.json)

#data="{\"issueKey\": \"CONDEC-1234\", \"ConDeTree\": {}}"
data={}
echo $data

sig=$(echo -n $data | openssl dgst -sha256 -hmac $WEBHOOK_SECRET | awk '{print "X-Hub-Signature: sha256="$2}')
echo $sig

curl -X POST -H "Content-Type: application/json" -H  "accept: application/json" -H "$sig" --data "$data" https://cuu-staging.ase.in.tum.de/api/v1/projects/ConDecDev/integrations/conDec

rm -f "${tmpfile}"
