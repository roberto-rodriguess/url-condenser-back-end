FROM debian:bookworm-slim
WORKDIR /app
COPY target/urlcondenser /app/urlcondenser
EXPOSE 8080
ENTRYPOINT ["/app/urlcondenser"]
