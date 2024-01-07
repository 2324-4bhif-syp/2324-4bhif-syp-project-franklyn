#!/bin/bash

./mvnw clean package;
java -jar target/openbox-1.0-SNAPSHOT-runner.jar
