#!/usr/bin/env sh

./gradlew clean buildImage


docker-compose up -d --build