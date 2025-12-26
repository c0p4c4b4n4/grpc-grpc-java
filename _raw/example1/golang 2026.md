Golang 2026
* База по backend
* Типы данных
  * Базовые
    * Boolean
      * сколько весит? 
    * Numeric
      * целые
        * знакавые 
          * int8 = byte
          * int16
          * int32 = rune -> int
          * int64 -> int
        * беззнакавые
            * uint8
            * uint16
            * uint32 -> uint
            * uint64 -> uint
      * дробные
        * float32
        * float64
        * complex64
        * complex128
    * String
      * что под капотом
      * выделяеися ли новая память при передаче в функцию?
      * иммутабельность Spring
      * перебор строки
  * Агрегатные
    * array
    * struct
      * сколько весит?
      * пустая структура
  * Интерфейстные
    * interface
  * Ссылочные
    * Pointer
    * Slice
      * len
      * cap
      * выделение нового стайса
      * релокация
        * коэффициент
    * Каналы
      * небуфферизированные
      * буфферизированные
      * приоитетные
      * однонаправленные
      * аксиомы чтения и записи каналов
    * Функции
      * методы для структур
    * Map
      * sync.Map
      * Map + RWMutex
      * колизии
      * потокобезопсность
      * потокобезопсные
        * Map + Mutex
        * Map + RWMutex
        * sync.Map
      * сложность
      * что под капотом у бакетов
      * эвакуация и перерачет хэша
      * новая версия мапы на Swiss Table
* Пакет synch
  * Mutex
  * RWMutex
  * sync.Map
    * методы
  * WaitGroup
  * Atomic
    * CAS механизм
  * Once
  * Locker
  * Pool
  * Atomic
* MGP модель
  * Machines
    * системный поток ОС
    * poller thread
  * Process
    * очередь из гороутин (LRQ)
    * глобальная очередь (GRQ)
    * активнная гороутина
    * work stealing
  * Goroutines
    * жизненный цикл
    * способ погасить гороутины
    * отличия от потока
    * утечка гороутин
    * heartbeat гороутин
    * время переключения гороутин
    * сколько места занимает?
    * сколько гороутин выделяить для обработки N запросов за M время ?
* Рантайм
  * Сборщик мусора
    * mark & sweep
    * настроки сбощика
      * GOGC
      * GOMEMLIMIT
      * GODEBUG
  * Планировщик
    * netpoller
      * epoll/kqueue/IOCP
      * syscall
    * многозадачность
      * вытесняющая
      * кооперативня
      * гибридная
    * синхронизация
    * отладка
  * Аллокация памяти
    * tcmalloc
    * Куча
    * Стек
    * mcache
      * локальный кэш памяти на каждый процесс
    * mcentral
      * общий пул блоков для всех процесов
    * mheap
      * глобальный менежер в всей памяти в процессе
  * Конструкции
    * Context
      * виды
        * Background
        * WithTimeout
        * WithCancel
        * WithDeadline
        * WithCancelClause
        * Todo
        * WithValue
    * Дженерики
    * make
      * make vs new vs {}
      * создает
        * Слайсы
        * Каналы
        * Мапы
    * var
    * :=
    * defer
    * select
      * CSP
  * Паттерны
    * Done-канал
    * Finished-канал
    * OR-канал
    * Pipeline
    * TЕE-канал
    * Очереди
    * Context
  * Скелеты
    * https://github.com/golang-standards/project-layout
  * Обработка ошибок
    * Паники
      * перехват (recover)
      * вызвать панику
    * значения, не исключения
    * сравнение ошибок
    * wrapping
    * пользовательские ошибки
    * сентинел ошибки
  * Инструменты
    * Фрейтворки
    * Тулкит
      * Gorilla
    * Роутеры
      * Gorilla/mux
      * chi
    * ORM
      * BUN
      * GORM
    * Драйверы
      * pgx
        * null mapping
        * RowToStructByName
    * Логирование
      * logrus
    * Линты
      * golanci-lint
        * staticcheck
        * errcheck
        * gosimple
        * govet
        * gosec
    * Профилирование
      * pprof
      * goleak
    * Тестирование
      * ginkgo
      * mockery
      * testify
      * cupolay
    * Хранение статики
      * hugo