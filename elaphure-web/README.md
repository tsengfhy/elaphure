# Elaphure Web

## Features

- Supports CORS
- Supports XSS
- Useful web utils
- OpenAPI integration

## Filters sequence

| Name                    | Order | Comment                                          |
|-------------------------|-------|--------------------------------------------------|
| CharacterEncodingFilter | MIN   | Setup character encoding                         |
| WebMvcMetricsFilter     | MIN+1 | Setup metrics tracking                           |
| FormContentFilter       | -9900 | Form processing                                  |
| RequestContextFilter    | -105  | Setup LocaleContextHolder / RequestContextHolder |
| CorsFilter              | -103  | CORS processing                                  |
| XssFilter               | -101  | XSS processing                                   |