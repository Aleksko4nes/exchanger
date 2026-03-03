FROM tomcat:10-jdk21

WORKDIR /usr/local/tomcat

RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY target/exchanger_v1-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

ENTRYPOINT ["catalina.sh", "run"]