call mvn -f fileserver-plugin-api/pom.xml clean install
call mvn -f java-plugin/pom.xml clean inatall
call mvn -DskipTests -Dspring-boot.run.profiles=run spring-boot:run 
