# Linking-entities-by-immutable-column

#### <br>Описание<br>
Необходимо получить функционал, которые позволил бы ссылаться на один столбец не только в обычном поле Entity, но и внутри @JoinColumn. 
Просто указать один и тот же столбец и полем в Entity и в @JoinColumn нельзя, поскольку Hibernate не будет знать как с этим работать. 
Поменяете вы это поле, а что делать со связной сущностью? Её тоже менять или просто не сохранять ваше изменение поля? И в обратную сторону так же. 
Сделали вы set новой сущности, а поле то по-прежнему имеет значение для старой сущности. Поэтому Hibernate хочет единолично управлять столбцами,
являющимися foreign key на другие сущности. 
Задача научить hibernate одновременно работать со столбцом как с обычным полем и использовать этот столбец как часть foreign key в @JoinColumn,
используя его во втором случае только на чтение. 

#### <br>Рассмотренные варианты реализации:<br>
1. У сущности на которую ссылаемся через @JoinColumn primary key - уникальный синтетический сгенерированный в самом сервисе id. 
   В таком случае присоединение сущности реализуется по unique набору полей. (linking-columns-entity-PK)
2. У сущности на которую ссылаемся через @JoinColumn primary key - составной первичный ключ.
   В таком случае присоединение сущности реализуется по её primary key. (linking-columns-NOT-entity-pk)

#### <br>Причины создания:<br>
Такая функциональность потребовалась, поскольку было необходимо иметь поле subSystemName внутри Entity и
производить связку сущностей по этому полю + ещё одному, отвечающему как раз за вторичную сущность. 
Использовать для связки сгенерированный уникальный Id (даже при его наличии) вторичной сущности нельзя,
поскольку в таком случае для сохранения основной сущности пришлось бы сделать подзапрос на получение вторичной сущности (или хотя бы её сгенерированного Id).
А сохранение основной сущности должно быть быстрым, 'как есть' и без подзапросов.

<br>`Реализации разделены по веткам, в main ветку слиты все ветки. Конкретные реализации нужно смотреть по отдельным веткам.`