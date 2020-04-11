#!/usr/bin/env bash

docker-compose exec heart-rate-connector bash -c '/scripts/create-pg-sink.sh'
