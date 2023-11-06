# Java-filmorate project
## Диаграмма БД и описание :
![Диаграмма БД](https://github.com/SAleksandrEr/java-filmorate/blob/main/Filmorate_DB_diagram.png)
### Описание таблиц:
> Table Film {
  unit_id integer [primary key, note: 'Id фильма']
  name_film varchar [note: 'Имя фильма']
  description_film varchar [note: 'Описание фильма']
  releaseDate_film date [note: 'Дата выхода фильма']
  duration_film  integer [note: 'Длительность фильма в минутах']
  mpa varchar [note: 'Возрастное ограничение - Motion Picture Association, сокращённо МРА:
  G — у фильма нет возрастных ограничений,
  PG — детям рекомендуется смотреть фильм с родителями,
  PG-13 — детям до 13 лет просмотр не желателен,
  R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
  NC-17 — лицам до 18 лет просмотр запрещён.']
}

Table Likes {
  likes_id integer [primary key, note: 'Id лайка']
  user_id integer  [ref: > User.user_id, note: 'Id пользователя кто поставил лайк']
  film_id integer [ref: > Film.unit_id, note: 'Id фильма связан с таблицей Film']
}

Table Genre {
  genre_id integer [primary key, note: 'Id жанра фильма']
  description_genre varchar [note: 'Описание жанра фильма - Комедия, Драма, Триллер, Документальный, Боевик']
  film_id integer [ref: > Film.unit_id, note: 'Id фильма связан с таблицей Film']
}

Table User {
  user_id integer [primary key, note: 'Id пользователя']
  email_user varchar [note: 'email пользователя']
  login_user varchar [note: 'Логин пользователя']
  name_user varchar  [note: 'Имя пользователя']
  birthday_user date [note: 'Дата рождения пользователя']
}

Table Friends {
  unit_id integer [primary key, note: 'уникальный id пользователя и его друга']
  friends_id integer [ref: > User.user_id, note: 'id друга связан с таблицей User'] 
  user_id integer [ref: > User.user_id, note: 'id пользователя связан с таблицей User']
  friendship_status varchar [note: 'Статус дружбы - unconfirmed, confirmed']
}
### SQL запросы :
