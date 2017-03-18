#!/bin/sh
mvn -DskipTests clean package
java -jar target/springboot-fileserver-*-SNAPSHOT.jar
