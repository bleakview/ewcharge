[![publish to docker registry](https://github.com/bleakview/ewcharge/actions/workflows/push_to_docker_hub.yml/badge.svg)](https://github.com/bleakview/ewcharge/actions/workflows/push_to_docker_hub.yml)

# A sample project with Micronaut and Kotlin

This is a sample project for micronaut and kotlin with mysql and redis as you can see on the title but also includes
sampling for the following :

- Two level build system for kotlin and docker
- Grafana
- Loki
- Prometheus
- Zipkin
- Compose ready for local test
- JWT Role based authentication
- Swagger UI support for testing
- Exposed SQL Framework

### Two level build system for kotlin and docker

How you build your system is pretty important for CI/CD. You either have a full system ready or can use docker two,
three ... level system for build. I like docker because every time it gives you a clean state which you know can run in
any environment.

### Grafana, Loki, Prometheus, Zipkin

You need an observability platform in order to watch your system. A sample configuration with docker compose is given
here so that a developer can see how the given system behaves on their system before prod.

### JWT Role based authentication

JWT is a great tool for authentication in microservices the system includes an authentication system using user roles.

### Swagger UI support for testing

Swagger UI is a wonderful method for rapid api testing for developers. But since it also generates OpenAPI you can
translate it to typescript type information which is a usually missed point which also helped Frontend developers to
catch bugs during coding instead of runtime.

### Exposed SQL Framework

While other ORM's exist for database interaction Exposed from Jetbrains let you use the real power of kotlin during
coding. I intentionally do not use magic mapper methods so that I can see errors during coding phase.

## Getting started

First use ```docker compose up``` method to start system. Open ```http://localhost:8080/swagger-ui/``` in browser. In
order to do anything you need to have a JWT token but in order you to have a JWT token you need to enter system. In
order to break this chicken egg problem an admin controller is created username is admin password is magic as you can
use createschema and createtestuser to bootstrap system.

After creating use curl to get authentication JWT token.

```
curl -X "POST" "http://localhost:8080/login" -H 'Content-Type: application/json' -d $'{"username": "admin","password":"admin"}'
```

The result will be something like

```json
{
  "username":"admin",
  "roles":[
    "ADMIN"
  ],
  "access_token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsIm5iZiI6MTY2NTY0ODg5Mywicm9sZXMiOlsiQURNSU4iXSwiaXNzIjoiZXdjaGFyZ2UiLCJleHAiOjE2NjU2NTI0OTMsImlhdCI6MTY2NTY0ODg5M30.UgXPZDe8yjrWwFwi-VaVc5P0wUnoFs2qcmgL0cxZztQ",
  "refresh_token":"eyJhbGciOiJIUzI1NiJ9.ZDAyZWZhOTAtMTI2Yy00ZDRjLWEzMzEtMzNhYWM3MDI3Nzlk.AUkZgr1gD3aRbbyRowZQaf37-bBCnemC6TS8zmW5E6A",
  "token_type":"Bearer",
  "expires_in":3600
}
```

copy the access_token

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsIm5iZiI6MTY2NTY0ODg5Mywicm9sZXMiOlsiQURNSU4iXSwiaXNzIjoiZXdjaGFyZ2UiLCJleHAiOjE2NjU2NTI0OTMsImlhdCI6MTY2NTY0ODg5M30.UgXPZDe8yjrWwFwi-VaVc5P0wUnoFs2qcmgL0cxZztQ
```

In Swagger UI click 'Authorization' button and paste the access_token there. You can use the other methods there.

Hashid is used in order to hide ids, although they are kept in database as long they are served as string in frontend 

## Docker
For docker image you can goto [https://hub.docker.com/r/bleakview/ewcharge](https://hub.docker.com/r/bleakview/ewcharge)
.
The docker image options are given below:
```
      PORT: 8080 #port number that will be served 
      MYSQL_URL: jdbc:mysql://mysql:3306/ #mysql uri 
      MYSQL_SCHEMA: ew_charge # name of scheme
      MYSQL_USER: root # mysql user name
      MYSQL_PASSWORD: 1 #  mysql password
      REDIS_URI: redis://redis:6379 #redis uri
      ZIPKIN_URL: http://zipkin:9411 #zipkin uri
      ZIPKIN_ENABLED: 'true' #if zipkin is enabled
      ZIPKIN_PROBABILITY: 1 # zipkin log probability
      HASHID_SALT: 8baa1388-8caf-4279-ac2c-54dd5bb825c1 # hashid salt
      HASHID_LENGTH: 10 # hashid length
      LOKI_URI: http://loki:3100/loki/api/v1/push # loki uri
      JWT_GENERATOR_SIGNATURE_SECRET: f79a3a1d-720b-4f68-a3a5-294a00ddc77f # jwt secret
      JWT_REFRESH_SIGNATURE_SECRET: f79a3a1d-720b-4f68-a3a5-294a00ddc77f # jwt refresh secret
```
If you see any errors or have questions don't hesitate to ask.

Happy coding.