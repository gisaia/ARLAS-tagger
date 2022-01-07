#!/bin/bash

################################################################################
# Functions
################################################################################

wait_for_hosts () {
  for host in ${@} ; do

  # Split "host" into "hostname" & "port" using bash parameter expansion
  hostname=${host%%:*}
  port=${host##*:}
  echo "waiting for ${hostname}"
  i=1; until nc -w 2 $hostname $port; do if [ $i -lt 30 ]; then sleep 1; else break; fi; i=$(($i + 1)); echo `date +%FT%T`' Trying to connect to '$hostname:$port; done
  done
}


################################################################################
# Script
################################################################################

set -e -o pipefail

IFS=',' read -r -a kafka_hosts <<< $1
IFS=',' read -r -a es_hosts <<< $2
IFS=',' read -r -a arlas_server_hosts <<< $3

wait_for_hosts ${kafka_hosts[@]} ${es_hosts[@]} ${arlas_server_hosts[@]}

java ${JVM_OPTION} -jar /opt/app/arlas-tagger.jar server /opt/app/configuration.yaml