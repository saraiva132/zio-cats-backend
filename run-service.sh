#!/bin/bash -e

## ./run-service.sh -d to clean images
## ./run-service.sh -dpbr to run service
## ./run-service.sh -drt to run tests with a clean service
## ./run-service.sh -t to run tests

while getopts 'dpbrt' flag; do
  case "${flag}" in
    d)
        docker-compose stop || true
        docker-compose rm -f || true
        ;;
    p)
        sbt clean universal:packageZipTarball
        ;;
    b)
        sbt clean docker:publishLocal
        ;;
    r)
        docker-compose -f docker-compose.yml up -d || true
        ;;
    t)
        sbt integrationTest
        ;;
    *)
        docker-compose -f docker-compose.yml up -d || true
        ;;
  esac
done


