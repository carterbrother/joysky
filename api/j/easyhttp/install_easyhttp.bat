@echo off
set JAVA_HOME=D:\tool\Java\jdk_17
set PATH=%JAVA_HOME%\bin;%PATH%
echo Using Java version:
java -version
echo.
echo Installing easy-http-spring to local repository (skipping tests)...
mvn clean install -DskipTests -f easyhttp-lib/easy-http-spring/easy-http-boot-starter/pom.xml
pause