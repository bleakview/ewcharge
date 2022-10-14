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

First use ```docker compose up``` method to start system. Open ```http://localhost:8080``` in browser. In order to do anything you need to have a JWT token but in order you to have a JWT token you need to enter system. In order to break this chicken egg problem an admin controller is created username is admin password is magic as you can use createschema and createtestuser to bootstrap system.

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


If you see any errors or have questions don't hesitate to ask.
Happy coding.