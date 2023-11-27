# Java-filmorate project
## Диаграмма БД и описание :
![Диаграмма БД](https://github.com/SAleksandrEr/java-filmorate/blob/main/Filmorate_DB_diagram.png)
### Описание таблиц:
#### > Film

_Table Film {\
   unit_id integer [primary key, note: 'Id фильма']\
   name_film varchar [note: 'Имя фильма']\
   description_film varchar [note: 'Описание фильма']\
   releaseDate_film date [note: 'Дата выхода фильма']\
   duration_film  integer [note: 'Длительность фильма в минутах']\
   mpa_id integer [ref: - Mpa.mpa_id, note: 'G, PG, PG-13, R, NC-17,\ 
   id Возрастное ограничение - Motion Picture Association, сокращённо МРА']\
}_
#### > Mpa
_Table Mpa {\
 mpa_id integer [primary key ]\
 name_mpa varchar(5) [note: 'G, PG, PG-13, R, NC-17']\
 duration_mpa varchar [note: 'Motion Picture Association, сокращённо МРА:\
 G — у фильма нет возрастных ограничений,\
 PG — детям рекомендуется смотреть фильм с родителями,\
 PG-13 — детям до 13 лет просмотр не желателен,\
 c,\
 NC-17 — лицам до 18 лет просмотр запрещён.']\
}_

#### > Likes

_Table Likes {\
  user_id integer  [ref: > User_filmorate.user_id, note: 'Id пользователя кто поставил лайк']\
  film_id integer [ref: > Film.unit_id, note: 'Id фильма связан с таблицей Film']\
}_

#### > Genre

_Table Genre {\
  genre_id integer [primary key, ref: > Genre_list.genre_id, note: 'Id жанра фильма связан с таблицей Genre_list']\
  film_id integer [ref: > Film.unit_id, note: 'Id фильма связан с таблицей Film']\
}_

#### > Genre_list
_Table Genre_list {\
  genre_id integer [primary key, note: 'Id жанра фильма связан с таблицей Genre']\
  description_genre varchar [note: 'Описание жанра фильма - Комедия, Драма, Триллер, Документальный, Боевик']\
}_

#### > User

_Table User_filmorate {\
  user_id integer [primary key, note: 'Id пользователя']\
  email_user varchar [note: 'email пользователя']\
  login_user varchar [note: 'Логин пользователя']\
  name_user varchar  [note: 'Имя пользователя']\
  birthday_user date [note: 'Дата рождения пользователя']\
}_

#### > Friends

_Table Friends {\
  unit_id integer [primary key, note: 'уникальный id пользователя и его друга']\
  friends_id integer [ref: > User_filmorate.user_id, note: 'id друга связан с таблицей User']\ 
  user_id integer [ref: > User_filmorate.user_id, note: 'id пользователя связан с таблицей User']\
  friendship_status varchar [note: 'Статус дружбы - unconfirmed, confirmed']\
}_

### SQL запросы :

#### > GET /users/

_SELECT user_id,
        email_user,
        login_user,
        name_user,
        birthday_user\
FROM User\
ORDER BY user_id;_ 

#### > GET /users/{id}

_SELECT user_id,
        email_user,
        login_user,
        name_user,
        birthday_user
FROM User\
WHERE user_id = {ID};_

#### > GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.

_SELECT u.user_id,
        u.email_user
        u.login_user
        u.name_user
        u.birthday_user\
FROM (SELECT f.friends_id\
FROM Friends AS f\
WHERE f.user_id = {id}\
      AND friendship_status = 'confirmed') AS userfr\
INNER JOIN User AS u ON u.user_id = userfr.friends_id\
ORDER BY u.user_id;_

#### > GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем. (список общих друзей)

_SELECT u.user_id
        u.email_user
        u.login_user
        u.name_user
        u.birthday_user
FROM (SELECT f.friends_id,
       COUNT(f.friends_id) AS noun\
FROM Friends AS f\
WHERE f.user_id IN ({id}, {otherid})\
      AND friendship_status = 'confirmed'\
GROUP BY f.friends_id\
HAVING noun > 1) AS userfr\
INNER JOIN User AS u ON u.user_id = userfr.friends_id\
GROUP BY u.user_id\
ORDER BY u.user_id;_

#### > GET /films/

_SELECT unit_id, 
       name_film,
       description_film,
       releaseDate_film,
       duration_film,
       genre_id\
FROM Film\
ORDER BY unit_id;_ 

#### > GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.

_SELECT f.unit_id, 
        f.name_film,
        f.description_film,
        f.releaseDate_film,
        f.duration_film\
FROM Film AS f\
INNER JOIN Likes AS l ON f.unit_id = l.film_id\
GROUP BY f.unit_id\
ORDER BY COUNT(unit_id) DESC\
LIMIT 10;_

#### > GET //films/{id}

_SELECT unit_id, 
        name_film,
        description_film,
        releaseDate_film,
        duration_film,
        genre_id\
FROM Film\
WHERE unit_id = {id};_
