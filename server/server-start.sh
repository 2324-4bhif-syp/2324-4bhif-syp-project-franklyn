#!/bin/bash

./mvnw clean package;
java -jar target/quarkus-app/quarkus-run.jar;
