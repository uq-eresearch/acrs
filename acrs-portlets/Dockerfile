#FROM openjdk:7-jdk
FROM ubuntu:xenial

RUN addgroup --system --gid 1000 liferay && \
        adduser --system --uid 1000 --gid 1000 liferay

ADD server-jre-8u152-linux-x64.tar.gz /opt
RUN chown --recursive root:root /opt/jdk1.8.0_152
ENV JAVA_HOME=/opt/jdk1.8.0_152
ENV JRE_HOME=/opt/jdk1.8.0_152/jre

ADD acrs-liferay.tar.gz /home/liferay

ADD ecj-4.5.jar /home/liferay/liferay-portal-5.2.3/tomcat-5.5.27/common/lib
RUN rm /home/liferay/liferay-portal-5.2.3/tomcat-5.5.27/common/lib/jasper-compiler-jdt.jar

ADD acrs-portlets.war /home/liferay/liferay-portal-5.2.3/deploy

RUN chown liferay:liferay /home/liferay/liferay-portal-5.2.3/deploy/acrs-portlets.war

WORKDIR /home/liferay

USER liferay

EXPOSE 8080

CMD ["/home/liferay/liferay-portal-5.2.3/tomcat-5.5.27/bin/catalina.sh", "run"]
