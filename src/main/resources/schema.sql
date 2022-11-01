CREATE TABLE film_genre (
                            film_id  INTEGER NOT NULL,
                            genre_id INTEGER NOT NULL
);

CREATE TABLE films (
                       film_id      INTEGER NOT NULL,
                       name         VARCHAR2(50) NOT NULL,
                       description  VARCHAR2(200),
                       release_date DATE NOT NULL,
                       duration     INTEGER NOT NULL,
                       mpa_id       INTEGER NOT NULL
);

ALTER TABLE films ADD CONSTRAINT films_pk PRIMARY KEY ( film_id );

CREATE TABLE friendships (
                             user_id   INTEGER NOT NULL,
                             friend_id INTEGER NOT NULL
);

ALTER TABLE friendships ADD CONSTRAINT friendships_pk PRIMARY KEY ( user_id,
                                                                    friend_id );

CREATE TABLE genre (
                       genre_id INTEGER NOT NULL,
                       name     VARCHAR2(50) NOT NULL
);

ALTER TABLE genre ADD CONSTRAINT genres_pk PRIMARY KEY ( genre_id );

CREATE TABLE likes (
                       film_id INTEGER NOT NULL,
                       user_id INTEGER NOT NULL
);

CREATE TABLE mpa_rating (
                            mpa_id INTEGER NOT NULL,
                            name   VARCHAR2(50) NOT NULL
);

ALTER TABLE mpa_rating ADD CONSTRAINT mpa_rating_pk PRIMARY KEY ( mpa_id );

CREATE TABLE users (
                       user_id  INTEGER NOT NULL,
                       email    VARCHAR2(50) NOT NULL,
                       login    VARCHAR2(50) NOT NULL,
                       name     VARCHAR2(50),
                       birthday DATE NOT NULL
);

ALTER TABLE users ADD CONSTRAINT users_pk PRIMARY KEY ( user_id );

ALTER TABLE film_genre
    ADD CONSTRAINT film_genre_films_fk FOREIGN KEY ( film_id )
        REFERENCES films ( film_id );

ALTER TABLE film_genre
    ADD CONSTRAINT film_genre_genres_fk FOREIGN KEY ( genre_id )
        REFERENCES genre ( genre_id );

ALTER TABLE films
    ADD CONSTRAINT films_mpa_rating_fk FOREIGN KEY ( mpa_id )
        REFERENCES mpa_rating ( mpa_id );

ALTER TABLE friendships
    ADD CONSTRAINT friendships_users_fk FOREIGN KEY ( user_id )
        REFERENCES users ( user_id );

ALTER TABLE friendships
    ADD CONSTRAINT friendships_users_fkv1 FOREIGN KEY ( friend_id )
        REFERENCES users ( user_id );

ALTER TABLE likes
    ADD CONSTRAINT likes_films_fk FOREIGN KEY ( film_id )
        REFERENCES films ( film_id );

ALTER TABLE likes
    ADD CONSTRAINT likes_users_fk FOREIGN KEY ( user_id )
        REFERENCES users ( user_id );