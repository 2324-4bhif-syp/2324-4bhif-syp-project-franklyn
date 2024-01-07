#!/bin/bash

./mvnw clean package;
java -jar target/recorder-1.0-SNAPSHOT-runner.jar
