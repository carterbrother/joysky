@echo off
set JAVA_HOME=D:\tool\Java\jdk_17
set PATH=%JAVA_HOME%\bin;%PATH%
echo Using Java version:
java -version
echo.
echo Starting easyhttp-appstart application...
mvn spring-boot:run -f easyhttp-appstart/pom.xml