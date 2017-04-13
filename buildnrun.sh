#!/usr/bin/env bash
mvn -DskipTests clean package
java -jar target/springboot-fileserver-*.jar
