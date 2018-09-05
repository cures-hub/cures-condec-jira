#!/bin/bash

WEBHOOK_SECRET=$1
API_ENDPOINT=$2

if [ "$#" -ne 2 ]; then
	echo "Please provide the secret token and the api endpoint"
    echo "Example: ./webhook SECRETTOKEN https://apiendpoint"
	exit 1
fi


refname=123456

tmpfile=$(mktemp --suffix=.json)

#data="{\"issueKey\": \"CONDEC-1234\", \"ConDeTree\": {}}"
data='{"issueKey": "CONDEC-1234", "ConDeTree": {"chart":{"container":"#treant-container","connectors":{"type":"straight"},"rootOrientation":"NORTH","levelSeparation":30,"siblingSeparation":30,"subTreeSeparation":30,"node":{"collapsable":true}},"nodeStructure":{"children":[{"children":[],"text":{"name":"Issue","title":"How can we detect homologous points on stereo images?","desc":"RAGRAM-2"},"HTMLclass":"problem","HTMLid":11605},{"children":[{"children":[{"text":{"name":"Alternative","title":"Use OpenCV library","desc":"RAGRAM-4"},"HTMLclass":"solution","HTMLid":11607}],"text":{"name":"Argument","title":"Users might not have installed this library","desc":"RAGRAM-7"},"HTMLclass":"rationale","HTMLid":11610},{"children":[],"text":{"name":"Constraint","title":"Users might not have installed this library","desc":"RAGRAM-5"},"HTMLclass":"context","HTMLid":11608}],"text":{"name":"Solution","title":"Use own implementation","desc":"RAGRAM-3"},"HTMLclass":"solution","HTMLid":11606},{"children":[{"children":[],"text":{"name":"Argument","title":"Less implementation effort","desc":"RAGRAM-6"},"HTMLclass":"rationale","HTMLid":11609}],"text":{"name":"Alternative","title":"Use OpenCV library","desc":"RAGRAM-4"},"HTMLclass":"solution","HTMLid":11607}],"text":{"name":"Decision","title":"Implement image matching based on ZNCC","desc":"RAGRAM-1"},"HTMLclass":"decision","HTMLid":11604},"graph":{"rootElement":{"id":11604,"summary":"Implement image matching based on ZNCC","description":"Image matching is implemented using ZNCC, as it is done here: https://martin-thoma.com/zero-mean-normalized-cross-correlation/","type":"Decision","key":"RAGRAM-1"}}}}'
data={\"issueKey\": \"CONDEC-1234\", \"ConDeTree\": {\"chart\":{\"container\":\"#treant-container\",\"connectors\":{\"type\":\"straight\"},\"rootOrientation\":\"NORTH\",\"levelSeparation\":30,\"siblingSeparation\":30,\"subTreeSeparation\":30,\"node\":{\"collapsable\":true}},\"nodeStructure\":{\"children\":[{\"children\":[],\"text\":{\"name\":\"Issue\",\"title\":\"How can we detect homologous points on stereo images?\",\"desc\":\"RAGRAM-2\"},\"HTMLclass\":\"problem\",\"HTMLid\":11605},{\"children\":[{\"children\":[{\"text\":{\"name\":\"Alternative\",\"title\":\"Use OpenCV library\",\"desc\":\"RAGRAM-4\"},\"HTMLclass\":\"solution\",\"HTMLid\":11607}],\"text\":{\"name\":\"Argument\",\"title\":\"Users might not have installed this library\",\"desc\":\"RAGRAM-7\"},\"HTMLclass\":\"rationale\",\"HTMLid\":11610},{\"children\":[],\"text\":{\"name\":\"Constraint\",\"title\":\"Users might not have installed this library\",\"desc\":\"RAGRAM-5\"},\"HTMLclass\":\"context\",\"HTMLid\":11608}],\"text\":{\"name\":\"Solution\",\"title\":\"Use own implementation\",\"desc\":\"RAGRAM-3\"},\"HTMLclass\":\"solution\",\"HTMLid\":11606},{\"children\":[{\"children\":[],\"text\":{\"name\":\"Argument\",\"title\":\"Less implementation effort\",\"desc\":\"RAGRAM-6\"},\"HTMLclass\":\"rationale\",\"HTMLid\":11609}],\"text\":{\"name\":\"Alternative\",\"title\":\"Use OpenCV library\",\"desc\":\"RAGRAM-4\"},\"HTMLclass\":\"solution\",\"HTMLid\":11607}],\"text\":{\"name\":\"Decision\",\"title\":\"Implement image matching based on ZNCC\",\"desc\":\"RAGRAM-1\"},\"HTMLclass\":\"decision\",\"HTMLid\":11604},\"graph\":{\"rootElement\":{\"id\":11604,\"summary\":\"Implement image matching based on ZNCC\",\"description\":\"Image matching is implemented using ZNCC, as it is done here: https://martin-thoma.com/zero-mean-normalized-cross-correlation/\",\"type\":\"Decision\",\"key\":\"RAGRAM-1\"}}}}
echo $data

sig=$(echo -n $data | openssl dgst -sha256 -hmac $WEBHOOK_SECRET | awk '{print "X-Hub-Signature: sha256="$2}')
echo $sig

curl -X POST -H "Content-Type: application/json" -H  "accept: application/json" -H "$sig" --data "$data" $API_ENDPOINT

rm -f "${tmpfile}"
echo ""

