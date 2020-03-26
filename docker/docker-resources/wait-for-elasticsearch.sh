#!/bin/sh -ex
set -e

script_name="$(basename "$(readlink -f "$0")")"

if ! [[ -z ${ARLAS_ELASTIC_NODES+x} ]]; then
  # ARLAS_ELASTIC_NODES is set
  # Will use ARLAS_ELASTIC_NODES
  first_elasticsearch_node=${ARLAS_ELASTIC_NODES%%,*}
  host=${first_elasticsearch_node%:*}

  case "$first_elasticsearch_node" in
    *:*)
      # port specified, extracting it
      port=${first_elasticsearch_node#*:}
      ;;
    *)
      # no port specified, using default
      port=9200
      ;;

  esac

else
  >&2 echo "[ERROR] $script_name -- ARLAS_ELASTIC_NODES is not set"
  exit 1
fi

i=1; until nc -w 2 $host $port; do if [ $i -lt 30 ]; then sleep 1; else break; fi; i=$(($i + 1)); echo "try to connect to $host:$port"; done