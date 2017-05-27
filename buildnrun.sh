#!/usr/bin/env bash
mvn -f fileserver-plugin-api/pom.xml clean install
mvn -f java-plugin/pom.xml clean package
mvn -DskipTests package

# spring-boot-maven-plugin does not work with external JARs in this project:

#java -Dloader.path=target/springboot-fileserver-1.0.0-SNAPSHOT.jar,java-plugin/target/javahtml-1.0.0-SNAPSHOT.jar -jar target/springboot-fileserver-1.0.0-SNAPSHOT.jar
#java -Dloader.path=java-plugin/target -jar target/springboot-fileserver-1.0.0-SNAPSHOT.jar
#java -Dloader.path=java-plugin/target/javahtml-1.0.0-SNAPSHOT.jar -jar target/springboot-fileserver-1.0.0-SNAPSHOT.jar
#java -cp target/springboot-fileserver-1.0.0-SNAPSHOT.jar:java-plugin/target/javahtml-1.0.0-SNAPSHOT.jar org.springframework.boot.loader.JarLauncher

# maven-shade-plugin works
java -cp target/springboot-fileserver-1.0.0-SNAPSHOT.jar:java-plugin/target/* -Dspring.profiles.active=run de.codereview.springboot.fileserver.Application

# not tried
# -Xbootclasspath/a
