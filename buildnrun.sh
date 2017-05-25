#!/usr/bin/env bash
mvn -f fileserver-plugin-api/pom.xml clean install
mvn -f java-plugin/pom.xml clean package
mvn -DskipTests package
#java -cp java-plugin/target/java-plugin-*.jar -jar target/springboot-fileserver-*.jar
#java -cp 'target/springboot-fileserver-*.jar:java-plugin/target/javahtml-*.jar' de.codereview.springboot.fileserver.Application
#java -Dloader.path=target/springboot-fileserver-1.0.0-SNAPSHOT.jar,java-plugin/target/javahtml-1.0.0-SNAPSHOT.jar -jar target/springboot-fileserver-1.0.0-SNAPSHOT.jar
java -Dloader.path=java-plugin/target -jar target/springboot-fileserver-1.0.0-SNAPSHOT.jar
#java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -jar target/springboot-fileserver-*.jar
