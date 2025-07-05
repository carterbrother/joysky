#!/usr/bin/env bash

mkdir -p /mnt/d/mysql/842/data /mnt/d/mysql/842/log | true

docker network create --driver bridge 1panel-network | true

docker rm -f mysql842 | true

docker-compose up -d

