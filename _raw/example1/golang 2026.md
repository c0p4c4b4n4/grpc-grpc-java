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
    * синхронизация
    * отладка
  * Аллокация памяти