Backend
* Computer Science
  * IPC межпроцессное взаимодействие
    * файл (все ОС)
    * сигналы (все ОС)
    * сокеты (большинство ОС)
    * конвейер, канал, pipe (POSIX)
    * именованный канал (POSIX)
    * неименованный канал (POSIX)
    * семафор (POSIX)
    * shared memory (POSIX)
    * обмен сообщениями
    * очередь сообщений
  * абстракции многопоточности
    * физическое ядро
    * виртуальное ядро
    * процесс
    * поток
    * легковесный поток
      * гороутины
      * короутины
      * файберы
      * гринтреады
    * асинхроность
    * конкурренция vs параллелизм
  * Межпоточное взаимодействие 
    * CSP
      * каналы
      * селекты
      * гороутины
    * Общая помять (shared memory and locks) 
* TCP/IP
  * HTTP
    * REST
      * Swagger docs
      * OpenAPI codegen
  * WebSockets
  * GraphQL
  * RPC
    * gRPC
    * SOAP
* Безопасность
  * Формат токенов
    * JWT
      * claims
      * roles
      * scopes
  * Авторизация
    * RBAC (на ролях)
    * ABAC (на аттрибутах)
    * ACL (на пользователях)
    * policy-based access (на правилах)
  * Аутентификация
    * Keycloak
    * OAuth 2.0 + OpenID Connect
      * реализует SSO
    * basic auth
    * SAML/Kerberos