#!/bin/bash

HOST=infrastructure.lab.example.com
USER=ftpuser1
PASS=w0rk1n

echo 'Deleting old journal files...'
rm -f orders/* &>/dev/null

echo 'Beginning file upload...'

# This avoid an error message if you try mrm * in an empty remote folder
REMOTE_FILES=$(lftp -c "open ${HOST} -u ${USER},${PASS} ; ls")
if [ "$REMOTE_FILES" != "" ]; then
	lftp -c "open ${HOST} -u ${USER},${PASS} ; mrm *" &>/dev/null
fi

lftp -c "open ${HOST} -u ${USER},${PASS} ; mput orders/incoming/*"

echo 'Upload complete!'
