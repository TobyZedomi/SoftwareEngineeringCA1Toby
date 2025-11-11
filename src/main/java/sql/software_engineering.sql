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
    review_id INT AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL,
    rating   double       NOT NULL,
    comment  varchar(255) NOT NULL,
    review_type  varchar(255) NOT NULL,
    PRIMARY KEY (review_id),
    FOREIGN KEY (username) REFERENCES users (username)
);


CREATE TABLE albumReview
(
    review_id           INT AUTO_INCREMENT,
    album_id INT        NOT NULL,
    PRIMARY KEY (review_id, album_id),
    FOREIGN KEY (review_id) REFERENCES review (review_id),
    FOREIGN KEY (album_id) REFERENCES album (album_id)

);


CREATE TABLE artistReview
(
    review_id           INT AUTO_INCREMENT,
    artist_id INT       NOT NULL,
    numberOfConcerts     INT NOT NULL,
    PRIMARY KEY (review_id, artist_id),
    FOREIGN KEY (review_id) REFERENCES review (review_id),
    FOREIGN KEY (artist_id) REFERENCES artist (artist_id)
);