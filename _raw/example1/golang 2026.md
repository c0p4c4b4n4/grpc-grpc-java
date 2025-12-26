Golang 2026
* Типы данных
  * Базовые
    * Boolean
      * сколько весит?
    * Numeric
      * целые
        * знаковые
          * int8 = byte
          * int16
          * int32 = rune (int)
          * int64 (int)
        * беззнакавые
          * uint8
          * uint16
          * uint32 (uint)
          * uint64 (uint)
      * дробные
        * float32
        * float64
        * complex64
        * complex128
    * String (!)
      * что под капотом (указатель на первый байт и длинна)
      * выделяеися ли новая память при передаче в функцию?
      * иммутабельность Spring
      * перебор строки
      * длинна строки
  * Агрегатные
    * array
    * struct
      * сколько весит?
      * пустая структура
  * Интерфейстные
    * interface
    * пустой интерфейс
    * джененрики
  * Ссылочные
    * Pointer
    * указатели vs ссылки
      * какой размер указателей в 32 и 64 бинтых системах
      * есть ли вообще ссылки в Go?
    * Slice aka динамичесский массив (!)
      * len
      * cap
      * выделение нового стайса
      * релокация
        * коэффициент
      * лайф-код
    * Каналы
      * небуферизированные
      * буферизированные
        * лайф-код
        * семафоры
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
* MGP модель
  * Machines
    * системный поток ОС
    * poller thread
  * Processor
    * очередь из гороутин (LRQ)
    * глобальная очередь (GRQ)
    * активнная гороутина
    * work stealing
  * Goroutines
    * отличия от потока
      * меньше весит
      * быстрее переключается
    * жизненный цикл
    * способ погасить гороутины
    * отличия от потока
    * утечка гороутин
    * heartbeat гороутин
    * время переключения гороутин
    * сколько места занимает?
    * сколько гороутин выделяить для обработки N запросов за M время?
* Рантайм
  * Сборщик мусора
    * mark & sweep
    * настроки сборщика
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
      * GODEBUG
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
        * WithValue
        * WithCancelClause
        * Todo
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
      * beego
      * Gin
      * Buffalo 
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
