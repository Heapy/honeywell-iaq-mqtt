# Container with application
FROM amazoncorretto:17.0.1
COPY /build/install/apx /app
ENTRYPOINT /app/bin/apx
