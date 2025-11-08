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