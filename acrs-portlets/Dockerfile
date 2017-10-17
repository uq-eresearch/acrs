FROM openjdk:7-jdk

RUN addgroup --system --gid 500 liferay && \
        adduser --system --uid 500 --gid 500 liferay

ADD acrs-liferay.tar.gz /home/liferay

ADD acrs-portlets.war /home/liferay/liferay-portal-5.2.3/deploy

RUN chown liferay:liferay /home/liferay/liferay-portal-5.2.3/deploy/acrs-portlets.war

WORKDIR /home/liferay

EXPOSE 8080

CMD ["/home/liferay/liferay-portal-5.2.3/tomcat-5.5.27/bin/catalina.sh", "run"]
