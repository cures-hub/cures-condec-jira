#!/bin/bash

WEBHOOK_SECRET=$1
API_ENDPOINT=$2

if [ "$#" -ne 3 ]; then
	echo "Please provide the secret token and the api endpoint"
    echo "Example: ./webhook SECRETTOKEN https://apiendpoint"
	exit 1
fi


refname=123456

tmpfile=$(mktemp --suffix=.json)

#data='{"issueKey": "CONDEC-1234", "ConDecTree": {"nodeStructure":{"children":[],"text":{"title":"Test Send","desc":"CONDEC-1234"}},"chart":{"container":"#treant-container","node":{"collapsable":"true"},"connectors":{"type":"straight"},"rootOrientation":"NORTH","siblingSeparation":30,"levelSeparation":30,"subTreeSeparation":30}}}'
data=$3
echo $data

sig=$(echo -n $data | openssl dgst -sha256 -hmac $WEBHOOK_SECRET | awk '{print "X-Hub-Signature: sha256="$2}')
echo $sig

#curl -v -X POST -H "Content-Type: application/json" -H  "accept: application/json" -H "$sig" --data "$data" $API_ENDPOINT
curl -v -X POST -H "Content-Type: application/json" -H  "accept: application/json" -H "$sig" --data "$data" $API_ENDPOINT  --trace-ascii /dev/stdout

rm -f "${tmpfile}"
echo ""

