FROM tomcat:10-jdk21

WORKDIR /usr/local/tomcat

COPY target/exchanger_v1-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

ENTRYPOINT ["catalina.sh", "run"]