**Роадмап подготовки к Java собеседованиям**

Создавая данный роадмап, я руководствовался двумя целями:

- собрать в одном месте все материалы для подготовки к собеседованиям
- подтолкнуть разработчиков к развитию вглубь и вширь

**Зачем нужны все материалы в одном месте?**

Потому что ключ к эффективной подготовке - это структура.

Когда у тебя каша в голове, очень легко впасть в прокрастинацию из-за отсутствия плана действий. Мозг сопротивляется, не знает за что хвататься.

Но когда у тебя есть план, мозгу очень легко взять одну проблему и разобраться в ней. Не нужно ничего искать, бояться что-то упустить. Бери и делай.

**Зачем нужно развиваться вглубь и вширь?**

Потому что простые задачи уже выполняет ИИ.

Просто гоняешь json туда-сюда? Ты ходишь по оху\*нно тонкому льду, мой друг.

Вникай в устройство JVM, понимай работу GC, различай вытесняющую и кооперативную многозадачность.

Умей проектировать и создавать надёжные, масштабируемые и поддерживаемые архитектуры.

Инженера отличает способность видеть картину в целом и принимать взвешенные технические решения.

*Роадмап не ставит целью предоставить развернутый список вопросов и ответов с собеседований. Потому что собеседование это не экзамен. Нет смысла заучивать ответы. Нужно понимать суть.*

*Роадмап постоянно обновляется, поэтому, чтобы быть в курсе изменений - подписывайтесь на блог.*

**Содержание:**

- Java Virtual Machine
- Java Garbage Collector
- Java Core
- Java Collection
- Java Stream
- Java Concurrency
- Spring Core
- Spring AOP
- Spring Data
- Spring Web
- Spring Security
- Spring Boot
- Spring Cloud
- SQL
- NoSQL
- Брокеры сообщений
- Docker, Kubernetes
- Паттерны проектирования, ООП, SOLID
- Алгоритмы и структуры данных
- Системный дизайн
- Soft skills

**Java Virtual Machine**

- Что происходит при старте java-приложения
- За счёт чего достигается кроссплатформенность
- Зачем нужен bytecode
- Что такое JIT
- Какие есть оптимизации
- Как происходит загрузка классов
- Какие гарантии даёт static и final
- Какие есть класслоадеры
- Какие есть области памяти
- Как хранится объект (его структура и заголовки)
- Зачем нужны разные виды ссылок
- Зачем нужен GraalVM

Что почитать:

<https://www.freecodecamp.org/news/jvm-tutorial-java-virtual-machine-architecture-explained-for-beginners/>

<https://blog.jamesdbloom.com/JVMInternals.html>

<https://www.azul.com/blog/understanding-java-compilation-from-bytecodes-to-machine-code/>

<https://www.ibm.com/docs/en/sdk-java-technology/8?topic=compiler-how-jit-optimizes-code>

<https://www.baeldung.com/java-memory-layout>

<https://shipilev.net/jvm/objects-inside-out/>

<https://www.baeldung.com/java-classloaders>

<https://antkorwin.com/concurrency/weakreference.html>

**Java Garbage Collector**

- Какие виды GC есть
- Как устроены механизмы их работы
- Под какую задачу какой GC выбрать
- Как настраивать параметры
- Как читать логи
- Какие метрики существуют
- Как снять heap dump
- Как анализировать heap dump
- Как ликвидировать утечки памяти

Что почитать:

<https://www.freecodecamp.org/news/garbage-collection-in-java-what-is-gc-and-how-it-works-in-the-jvm/>

<https://dataintellect.com/blog/low-latency-java-optimisation-through-garbage-collector-tuning/>

<https://sematext.com/blog/java-garbage-collection-logs/>

<https://blog.gceasy.io/simple-effective-g1-gc-tuning-tips/>

<https://sematext.com/blog/java-memory-leaks/>

**Java Core**

- [Что происходит, когда мы пишем new](https://boosty.to/backend_interviewer/posts/a750ae22-1309-42e5-8d7f-b8a0c33baa7a)
- [hashCode() по умолчанию](https://boosty.to/backend_interviewer/posts/aa2ac273-7ccf-454d-a21b-fd93c0e51174)
- Как работать с рефлексией
- Зачем нужны свои аннотации
- Как работает сериализация/десериализация
- Какие проблемы несёт autoboxing
- Чем отличаются string pool и integer pool
- Почему в kotlin и scala нет проверяемых исключений
- Как работают функциональные интерфейсы
- Зачем нужны record
- Какие возможности даёт nio

Что почитать:

<https://www.baeldung.com/java-custom-annotation>

<https://www.guvi.in/blog/guide-for-serialization-in-java/>

<https://thospfuller.com/2024/03/12/java-autoboxing-performance/>

<https://stackabuse.com/exception-handling-in-java-a-complete-guide-with-best-and-worst-practices/>

**Java Collection**

- Внутреннее устройство всех коллекций
- Какие существуют виды разрешения коллизий
- Какая алгоритмическая сложность вставки, доступа, поиска
- Отличия fail-fast и fail-safe итерирования
- Как выбрать коллекцию под задачу
- Как работают потокобезопасные коллекции
- Какие существуют коллекции из сторонних библиотек
- Дженерики: стирание типов, extends, super, pecs

Что почитать:

<https://struchkov.dev/blog/ru/java-collection-framework/>

<https://www.baeldung.com/java-fail-safe-vs-fail-fast-iterator>

<https://jackiewicz.hashnode.dev/understanding-hashing-and-collisions-in-java-collections-the-role-of-hashcode-and-equals>

<https://dev.mo4tech.com/the-most-complete-guide-to-java-generics.html>

**Java Stream**

- Внутреннее устройство стримов
- За счёт чего реализуется ленивость
- Ограничения параллельных стримов

Что почитать:

<https://struchkov.dev/blog/ru/java-stream-api/>

<https://www.baeldung.com/java-fork-join>

**Java Concurrency**

- Чем отличаются параллельность, многопоточность и ассинхронность
- Какие существуют подходы к организации многозадачности
- Вытесняющая и кооперативная многозадачность
- Как реализуется многопоточность на одноядерном процессоре
- Как многопоточность в java соотносится с ОС
- Какие проблемы многопоточности есть
- [Что такое data race, race condition, deadlock, livelock, starvation](https://boosty.to/backend_interviewer/posts/35d6606b-55f9-4654-9907-354e5a94ca22)
- Какие гарантии даёт JMM и happens-before
- Где хранится информация о блокировке synchronized
- Почему volatile не подходит для потокобезопасного счётчика
- [Как сделать потокобезопасный счётчик](https://boosty.to/backend_interviewer/posts/eaad5028-b081-410e-9994-b855bfe72274)
- Как реализованы атомики (cas, faa)
- Как устроены блокировки и синхронизаторы
- [Устройство ThreadPool](https://boosty.to/backend_interviewer/posts/da3664fa-0edd-4d7b-86c9-2f1001d0a701)
- Особенности работы ForkJoinPool
- Как выбрать размер для пула потоков
- Отличия обычных потоков от виртуальных потоков, потоков из reactor и корутин из kotlin
- Проблемы виртуальных потоков
- Как снять thread dump
- Как анализировать thread dump

Что почитать:

<https://boosty.to/backend_interviewer/posts/2d01bd46-f3ed-4e60-8a92-c5781e563cd4>

<https://shameekagarwal.github.io/posts/java-multithreading/>

<https://jenkov.com/tutorials/java-concurrency/index.html>

<https://shipilev.net/blog/2014/jmm-pragmatics/>

**Spring Core**

- Что происходит при старте spring-приложения
- Почему приложение долго запускается
- Жизненный цикл контекста и бинов
- Как вмешаться в процесс создания бинов
- Как создать бин в runtime
- Какое внедрение бинов лучше
- Чем отличаются Inversion of Control и Dependency Injection

Что почитать:

<https://dev.to/rahul_talatala/spring-core-fundamentals-a-beginner-guide-3daa>

<https://habr.com/ru/articles/720794/>

**Spring AOP**

- Чем отличаются Spring AOP и AspectJ
- Когда происходит создание прокси
- Какие виды прокси есть
- Чем отличаются JDK Dynamic Proxy, CGLIB и Byte Buddy

Что почитать:

<https://www.appsdeveloperblog.com/a-guide-to-spring-boot-aop-to-record-user-operations/>

<https://docs.spring.io/spring-framework/reference/core/aop/proxying.html>

**Spring Data**

- Как методы из интерфейсов расширяющих JpaRepository превращаются в запросы к БД
- N+1 проблема
- Плюсы и минусы Hibernate
- Кэши Hibernate
- Кэш сгенерированных запросов
- Жизненный цикл entity
- Какой equals/hashCode должен быть у entity
- Как настроить таймаут запросов
- Как бороться с инъекциями
- Всё про Transactional
- Зачем нужен HikariPool

Что почитать:

<https://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate>

<https://blog.frankel.ch/digging-hibernate-query-cache/>

<https://dev.to/easycat/zoopark-hibernate-n1-zaprosov-ili-kak-nakormit-zhadnogho-bieghiemota-26nn>

<https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth>

**Spring Web**

- Жизненный цикл входящего запроса
- Чем отличаются webmvc и webflux
- Какой размер пула потоков в webmvc и webflux
- Как применять api-first и code-first подходы
- Что за подход rest api
- Как валидировать входящий запрос
- Как перехватить входящий запрос
- Как обрабатывать ошибки во время выполнения входящего запроса
- Как подходить к версионированию запросов
- Как с бэкенда отправить запрос на фронтенд

Что почитать:

<https://habr.com/ru/articles/565698/>

<https://habr.com/ru/articles/716544/>

**Spring Security**

- Чем отличаются идентификация, аутентификация и авторизация
- Типы Basic, JWT, LDAP, OAuth2
- Зачем нужны CSRF, CORS
- Как хранить пароли в БД и в коде
- Является ли использование spring security анти-паттерном

Что почитать:

<https://www.marcobehler.com/guides/spring-security>

<https://syskool.com/cors-csrf-and-secure-headers-in-spring-boot/>

**Spring Boot**

- Зачем нужен spring boot
- Зачем нужен spring actuator
- Как устроены стартеры
- Как spring понимает, что библиотека является стартером
- Сканируется ли спринг все библиотеки и весь код
- Нужны ли профили

Что почитать:

<https://www.marcobehler.com/guides/spring-boot-autoconfiguration>

<https://www.baeldung.com/spring-boot-custom-starter>

**Spring Cloud**

- Как централизованно хранить конфиги
- Как безопасно хранить пароли
- Как сервисам находить друг друга если их по несколько инстансов
- Как входящий запрос понимает в какой сервис идти
- Как ограничить кол-во запросов из одного сервиса в другой
- Как проследить цепочку запросов между сирвисами и время их выполнения

Что почитать:

<https://habr.com/ru/articles/793550/>

<https://www.baeldung.com/spring-cloud-sleuth-single-application>

**SQL**

- Что значит реляционная база данных
- Как БД хранит данные в файловой системе
- Как БД хранит индексы
- Какие виды индексов существуют и как выбрать правильный
- Как анализировать план запроса
- Что обозначает ACID
- Уровни изолированности транзакций
- Оптимистическая и пессимистическая блокировки

Что почитать:

<https://habr.com/ru/articles/555920/>

[https://ru.wikipedia.org/wiki/Уровень_изолированности_транзакций](https://ru.wikipedia.org/wiki/%D0%A3%D1%80%D0%BE%D0%B2%D0%B5%D0%BD%D1%8C_%D0%B8%D0%B7%D0%BE%D0%BB%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D0%BE%D1%81%D1%82%D0%B8_%D1%82%D1%80%D0%B0%D0%BD%D0%B7%D0%B0%D0%BA%D1%86%D0%B8%D0%B9)

<https://neon.com/postgresql/postgresql-indexes>

<https://habr.com/ru/articles/203320/>

**NoSQL**

- Какие виды БД существуют и как выбрать правильную
- Какую БД выбрать для быстрой записи
- Какую БД выбрать для быстрого чтения
- Что обозначает BASE и чем отличается от ACID
- Механизмы репликации, шардирования и отказоустойчивости

Что почитать:

<https://www.designgurus.io/course-play/grokking-the-system-design-interview/doc/acid-vs-base-properties-in-databases>

<https://www.educative.io/blog/database-scalability-sharding-partitioning-replication>

**Брокеры сообщений**

- Чем отличаются синхронные и асинхронные взаимодействия
- Почему многие используют Apache Kafka
- Чем kafka отличается от остальных
- Какая архитектура у kafka
- Какие гарантии даёт kafka
- Как гарантированной отправить и обработать сообщение без потерь и дублей
- Паттерны transactional outbox, идемпотентный потребитель, dead letter queue
- Как обеспечить порядок обработки сообщений
- Как подходить к версионированию сообщений

Что почитать:

<https://boosty.to/backend_interviewer/posts/9bf8503f-8864-4272-a66f-3f8348b18f1b>

<https://docs.arenadata.io/ru/ADStreaming/current/concept/architecture/kafka/delivery_guarantees.html>

<https://habr.com/ru/companies/lamoda/articles/678932/>

**Docker, Kubernetes**

- Зачем нужны контейнеры
- Зачем нужен оркестратор контейнеров
- Отличия Deployment, ReplicaSet, Pod
- Зачем нужен Service
- Что за StatefulSet
- Как посмотреть логи приложения в контейнере
- Как снять heapdump/threaddump в контейнере
- Как выделять ресурсы контейнеру
- Сколько памяти в контейнере выдяется jvm по умолчанию

**Паттерны проектирования, ООП, SOLID**

- Инкапсуляция, наследование, полиморфизм, абстракция
- Чем отличаются статический и динамический полиморфизм
- Чем отличаются агрегация и композиция
- Принципы единственной ответственности, открытости/закрытости, подстановки Барбары Лисков, разделения интерфейсов, инверсии зависимостей
- Пораждающие паттерны: фабричный метод, абстрактная фабрика, билдер, прототип, синглтон
- Структурные паттерны: адаптер, декоратор, фасад, прокси, компоновщик
- Поведенческие паттерны: стратегия, наблюдатель, шаблонный метод, цепочка обязанностей
- Какие паттерны используются в Spring

Что почитать:

<https://bool.dev/blog/detail/gof-design-patterns>

<https://raygun.com/blog/oop-concepts-java/>

<https://solidbook.vercel.app/>

**Алгоритмы и структуры данных**

- Сравнение сложностей алгоритмов: констнатная, логарифмическая, линейная, квадратичная, экспоненциальная, факториальная
- Линейные структуры данных: массив, связный список, стек, очередь
- Нелинейные структуры данных: хеш-таблица, дерево (бинарное, сбалансированное, куча), граф
- Базовые алгоритмы: простые сортировки (пузырьком, выбором, вставками), эффективные сортировки (быстрая, слиянием), линейный поиск, бинарный поиск, два указателя, скользящее окно, обход в ширину/глубину
- Жадные алгоритмы, динамическое программирование, разделяй и властвуй, бэктрекинг

Что почитать:

<https://boosty.to/backend_interviewer/posts/084dbd6e-8fc4-4263-b5fd-3f04e98de7f0>

<https://boosty.to/backend_interviewer/posts/3ba3527e-84cb-40e2-a5ea-c3ed549654f1>

<https://boosty.to/backend_interviewer/posts/2875214e-ddc1-412b-89a2-1f1e0b1ee8d0>

<https://boosty.to/backend_interviewer/posts/be4bb44c-8b91-42f2-9d26-d9a1a1c18251>

**Системный дизайн**

- Вертикальное vs горизонтальное масштабирование
- Балансировка нагрузки
- SQL vs NoSQL
- Репликация и шардирование
- Кэширование
- Сети доставки контента
- Задержка vs пропускная способность
- CAP-теорема
- Согласованность данных: strong и eventual
- tcp/ip, http/https, rpc
- REST vs GraphQL
- Монолит vs микросервис
- Идемпотентность
- Практика: сокращатель ссылок, лента новостей, мессенджер, сервис такси, видеохостинг

Что почитать:

<https://bytebytego.com/courses/system-design-interview/scale-from-zero-to-millions-of-users>

**Soft skills**

- Какие существуют методологии разработки
- Умение объяснять архитектурные решения, trade-off’ы, помогать коллегам
- Умение презентовать опыт: STAR техника, причины использования тех или иных решений

