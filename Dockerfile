FROM gcr.io/distroless/cc-debian12
WORKDIR /app
COPY target/urlcondenser /app/urlcondenser
EXPOSE 8080
ENTRYPOINT ["/app/urlcondenser"]
