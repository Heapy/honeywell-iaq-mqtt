# Container with application
FROM bellsoft/liberica-openjdk-centos:11
COPY /build/install/apx /app
ENTRYPOINT /app/bin/apx
