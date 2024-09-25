drop table if exists genres_of_films, likes, friends, films, users, genres, rating;

create table genres(
    id int generated always as identity not null primary key,
    genre varchar(255)
);

create table rating(
    id int generated always as identity not null primary key,
    mpa varchar(5)
);

create table films(
    id int generated always as identity not null primary key,
	name varchar(255) not null,
	description varchar(255),
	release_date date,
	duration int,
	rating_id int references rating(id) on delete set null
);

create table users(
    id int generated always as identity not null primary key,
    email varchar(255) not null,
    login varchar(255) not null,
    name varchar(255) not null,
    birthday date
);

create table likes(
    id int generated always as identity not null primary key,
    user_id int references users(id) on delete cascade,
    film_id int references films(id) on delete cascade
);

create table friends(
    id int generated always as identity not null primary key,
    user_id int references users(id) on delete cascade,
    friend_id int references users(id) on delete cascade,
    friendship boolean
);

create table genres_of_films(
    id int generated always as identity not null primary key,
    genre_id int references genres(id) on delete cascade,
    film_id int references films(id) on delete cascade
);
