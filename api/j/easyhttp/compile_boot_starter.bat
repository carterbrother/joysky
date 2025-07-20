@echo off
set JAVA_HOME=D:\tool\Java\jdk_17
set PATH=%JAVA_HOME%\bin;%PATH%
echo Using Java version:
java -version
echo.
echo Compiling easy-http-boot-starter module...
mvn clean compile -f easyhttp-lib/easy-http-spring/easy-http-boot-starter/pom.xml
pause