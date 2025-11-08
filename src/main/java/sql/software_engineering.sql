DROP
DATABASE IF EXISTS software_engineering;
CREATE
DATABASE IF NOT EXISTS software_engineering;

USE
software_engineering;

CREATE TABLE users
(
    username varchar(50) UNIQUE NOT NULL,
    password varchar(255)       NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE genre
(
    genre_id   INT AUTO_INCREMENT,
    genre_name varchar(255) NOT NULL,
    PRIMARY KEY (genre_id)
);

CREATE TABLE artist
(
    artist_id     INT AUTO_INCREMENT,
    artist_name   varchar(255) NOT NULL,
    genre_id      INT(11) NOT NULL,
    overview      varchar(255) NOT NULL,
    date_of_birth DATE         NOT NULL,
    PRIMARY KEY (artist_id),
    FOREIGN KEY (genre_id) REFERENCES genre (genre_id)

);


CREATE TABLE album
(
    album_id        INT AUTO_INCREMENT,
    album_name      varchar(255) NOT NULL,
    artist_id       INT(11) NOT NULL,
    description     varchar(255) NOT NULL,
    date_of_release DATE NOT NULL,
    PRIMARY KEY (album_id),
    FOREIGN KEY (artist_id) REFERENCES artist (artist_id)
);

CREATE TABLE review
(
    username VARCHAR(50)  NOT NULL,
    album_id INT          NOT NULL,
    rating   double       NOT NULL,
    comment  varchar(255) NOT NULL,
    PRIMARY KEY (username, album_id),
    FOREIGN KEY (username) REFERENCES users (username)
)