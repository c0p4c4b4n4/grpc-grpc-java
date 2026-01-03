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
* Микросервисы
  * плюсы и минусы
  * микросервисы vs монолиты
  * паттерны микросервисов
    * обмен данными
      * CQRS
      * Saga
      * Event sourcing
      * database per service
      * transactional outbox
      * transactional inbox
      * eventual consistency
        * теорема CAP 
    * коммуникация
      * API gateway
      * backend for frontend
      * Service Mesh
      * Request/response vs Event-driven
    * мониторинг
      * health check
      * centralized logging
      * distributed tracing
    * отказоустойчивость
      * retry
      * circuit breaker
      * bulkhead
    * обнаружение
      * на стороне клиента
      * на стороне сервера
      * sidecar pattern
    * развертывание
      * канарейки
      * feature toggle
      * rolling update
      * blue-greed deployment
  * взаимодействие
    * синхронное
      * REST
      * WebSocket
      * gRPC
      * graphQL
    * асинхронное
      * Kafka
      * Rabbit
      * Nats
      * Nats JetStream
      * Redis Streams
      * Redis Pub/Sub
* Деплой и мониторинг
  * мониторинг
    * Grafana
      * это UI
    * Zabbix
    * Prometheus
    * VictoriaMetrics
  * логирование
    * ELK-стек
      * Elstic
      * LogStash
      * Kibana
  * развертывание
    * Docker
    * Gitlab CI/CD
    * Kubernetes
      * kubectl
      * ktx
    * Ansible
    * Jenkins
  * тестирование
    * unit
      * снапшот
    * интеграционное
    * e2e
  * бэкапы
    * S3
    * PITR
    * DB snapshots
  * трассировка
    * Jaeger
    * OpenTelemetry
    * Zipkin
  * балансировка
    * Nginx
    * Envoy
  * облака
    * AWS
      * S3
      * SQS
    * GCP
    * Azure
* Архитектура
  * Паттерны проектирования
    * порождающие
      * синглтон
      * фабричный метод
      * абстактная фабрика
      * прототип
      * объектный пул
      * отложенная инициализация
      * мультитон
    * структурные
      * компоновщик
      * декоратор
      * фасад
      * адаптер
      * мост
      * приспособленец
      * заместитель (прокси)
      * единая точка входа
    * поведенческие
      * команды
      * стратегия
      * наблюдатель
      * посетитель
      * интрепретатор
    * многопоточного программирования
      * блокировка
      * планировщик
      * монитор
      * read/write lock
      * thread pool
      * однопоточное выполнение
      * блокировка с двойной проверкой
    * баз данных
      * Repository
      * Active Record
      * Data Mapper
  * DDD