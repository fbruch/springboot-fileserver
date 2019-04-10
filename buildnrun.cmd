call mvn -f fileserver-plugin-api/pom.xml clean install
call mvn -f java-plugin/pom.xml clean package
call mvn -DskipTests package

rem spring-boot-maven-plugin does not work with external JARs in this project:

rem java -Dloader.path=target/springboot-fileserver-1.0.0-SNAPSHOT.jar,java-plugin/target/javahtml-1.0.0-SNAPSHOT.jar -jar target/springboot-fileserver-1.0.0-SNAPSHOT.jar
rem java -Dloader.path=java-plugin/target -jar target/springboot-fileserver-1.0.0-SNAPSHOT.jar
rem java -Dloader.path=java-plugin/target/javahtml-1.0.0-SNAPSHOT.jar -jar target/springboot-fileserver-1.0.0-SNAPSHOT.jar
rem java -cp target/springboot-fileserver-1.0.0-SNAPSHOT.jar:java-plugin/target/javahtml-1.0.0-SNAPSHOT.jar org.springframework.boot.loader.JarLauncher

rem  maven-shade-plugin works

java -cp target/springboot-fileserver-1.0.0-SNAPSHOT.jar;java-plugin/target/* -Dspring.profiles.active=run de.codereview.springboot.fileserver.Application

rem  not tried
rem  -Xbootclasspath/a
