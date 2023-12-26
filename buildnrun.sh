#!/usr/bin/env bash
mvn -f fileserver-plugin-api/pom.xml clean install
mvn -f java-plugin/pom.xml clean install
mvn -DskipTests -Dspring-boot.run.profiles=run spring-boot:run 
