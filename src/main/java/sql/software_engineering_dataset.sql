INSERT INTO `users` (`username`, `password`)
VALUES  ('Toby', '$2a$12$al0T3VLHPQM4kiZqgyitj.rDgVDSI1xc5y2SCkbo8ZJDTJ5WOZl5a'),
        ('Sean', '$2a$12$al0T3VLHPQM4kiZqgyitj.rDgVDSI1xc5y2SCkbo8ZJDTJ5WOZl5a'),
        ('Sarah', '$2a$12$al0T3VLHPQM4kiZqgyitj.rDgVDSI1xc5y2SCkbo8ZJDTJ5WOZl5a');


INSERT INTO `genre` (`genre_id`, `genre_name`)
VALUES  (1, 'Hip Hop'),
        (2, 'Rock'),
        (3, 'Pop');


INSERT INTO `artist` (`artist_id`, `artist_name`, `genre_id`, `overview`, `date_of_birth`)
VALUES (1, 'Kendrick Lamar', 1, 'Kendrick Lamar is an American rapper, singer, songwriter, and record producer', '1987-02-16'),
       (2, 'Jimi Hendrix', 2, ' Hendrix was an American guitarist, singer, and songwriter', '1957-04-11'),
       (3, 'Nas', 1, 'Nas, is an American rapper and entrepreneur.', '1975-02-16'),
       (4, 'Taylor Swift', 3, 'Taylor Swift is an American singer-songwriter.', '1989-02-16');


INSERT INTO `album` (`album_id`, `album_name`, `artist_id`, `description`, `date_of_release`)
VALUES (1, 'To Pimp A Butterfly', 1, 'Modern Day Classic', '2015-03-16'),
       (2, 'DAMN', 1, 'Changed Hip Hop', '2017-05-22'),
       (3, 'Electric Ladyland', 2, 'Some say the Greatest album ever', '1967-10-16'),
       (4, 'Illmatic', 3, 'Everything perfect about Hip Hop', '1994-04-10'),
       (5, 'Red', 4, 'Perfect Pop Album', '2012-10-10');


INSERT INTO `review` (`username`, `album_id`, `rating`, `comment`)
VALUES ('Toby', 1, 10, 'Love this album'),
       ('Toby', 2, 9, 'Wonderful'),
       ('Sean', 3, 10, 'Best ever'),
       ('Sean', 4, 8, 'Great album'),
       ('Sarah', 5, 9, 'Pop excellence');
