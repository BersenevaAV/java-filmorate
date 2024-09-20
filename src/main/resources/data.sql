INSERT INTO genres (genre) VALUES('Комедия');
INSERT INTO genres (genre) VALUES('Драма');
INSERT INTO genres (genre) VALUES('Мультфильм');
INSERT INTO genres (genre) VALUES('Триллер');
INSERT INTO genres (genre) VALUES('Документальный');
INSERT INTO genres (genre) VALUES('Боевик');

INSERT INTO rating (mpa) VALUES('G');
INSERT INTO rating (mpa) VALUES('PG');
INSERT INTO rating (mpa) VALUES('PG-13');
INSERT INTO rating (mpa) VALUES('R');
INSERT INTO rating (mpa) VALUES('NC-17');

INSERT INTO films (name, description, release_date, duration, rating_id) VALUES('film1', 'descr1', '2020-11-03', 120, 4);
INSERT INTO films (name, description, release_date, duration, rating_id) VALUES('film2', 'descr2', '2014-6-28', 90, 2);
INSERT INTO films (name, description, release_date, duration, rating_id) VALUES('film3', 'descr3', '2000-02-17', 145, 5);

INSERT INTO users (email, login, name, birthday) VALUES('email1@m.ru', 'login1', 'user1', '2000-03-14');
INSERT INTO users (email, login, name, birthday) VALUES('email2@m.ru', 'login2', 'user2', '2005-02-20');
INSERT INTO users (email, login, name, birthday) VALUES('email3@m.ru', 'login3', 'user3', '1997-10-28');
INSERT INTO users (email, login, name, birthday) VALUES('email4@m.ru', 'login4', 'user4', '2013-11-20');

INSERT INTO likes (user_id, film_id) VALUES(1, 3);
INSERT INTO likes (user_id, film_id) VALUES(1, 2);
INSERT INTO likes (user_id, film_id) VALUES(3, 1);
INSERT INTO likes (user_id, film_id) VALUES(4, 1);
INSERT INTO likes (user_id, film_id) VALUES(4, 3);
INSERT INTO likes (user_id, film_id) VALUES(2, 1);

INSERT INTO friends (user_id, friend_id, friendship) VALUES(1, 2, false);
INSERT INTO friends (user_id, friend_id, friendship) VALUES(1, 4, true);
INSERT INTO friends (user_id, friend_id, friendship) VALUES(2, 4, false);
INSERT INTO friends (user_id, friend_id, friendship) VALUES(2, 3, true);
INSERT INTO friends (user_id, friend_id, friendship) VALUES(3, 4, false);

INSERT INTO genres_of_films (film_id, genre_id) VALUES(1, 1);
INSERT INTO genres_of_films (film_id, genre_id) VALUES(1, 4);
INSERT INTO genres_of_films (film_id, genre_id) VALUES(1, 6);
INSERT INTO genres_of_films (film_id, genre_id) VALUES(2, 3);
INSERT INTO genres_of_films (film_id, genre_id) VALUES(3, 5);