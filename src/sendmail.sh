#!/bin/bash

#takes an email address and a message as args
#basically simplifies use of the mailx command so it can be more easily integrated into the java code

if [ "$1" -lt 3 ]; then	
	exit
fi

EMAIL = $1
MSG = $2

echo "$MSG" | mailx -s "AutoAware Notification" $EMAIL