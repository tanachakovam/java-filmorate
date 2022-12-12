create table IF NOT EXISTS MPA
(
    mpa_id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    mpa_name varchar(55)
);

create table IF NOT EXISTS genres
(
    genre_id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    genre_name varchar(20)
);

create table IF NOT EXISTS film_genres
(
    film_id  int,
    genre_id int,
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id)

);


create table IF NOT EXISTS films
(
    id           INTEGER PRIMARY KEY AUTO_INCREMENT,
    film_name    varchar(100) NOT NULL,
    description  varchar(200),
    release_date date,
    duration     int,
    mpa_id       int,
    FOREIGN KEY (mpa_id) REFERENCES MPA (mpa_id)
);



create table IF NOT EXISTS users
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    email    varchar(55),
    login    varchar(40) NOT NULL,
    username varchar(100),
    birthday date
);

create table IF NOT EXISTS friendship
(
    user_id   int,
    friend_id int,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);

create table IF NOT EXISTS likes
(
    user_id int,
    film_id int,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (film_id) REFERENCES films (id)
);

