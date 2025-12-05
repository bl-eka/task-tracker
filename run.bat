@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot
call mvnw.cmd spring-boot:run -Dserver.port=8083
