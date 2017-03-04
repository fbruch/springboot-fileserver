#!/bin/sh
mvn clean package
java -jar target/springboot-fileserver-0.0.1-SNAPSHOT.jar
