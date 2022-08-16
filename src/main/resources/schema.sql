
DROP TABLE IF EXISTS FILM_GENRES CASCADE ;
DROP TABLE IF EXISTS FILMS CASCADE ;
DROP TABLE IF EXISTS FRIENDS CASCADE ;
DROP TABLE IF EXISTS GENRE_NAMES CASCADE ;
DROP TABLE IF EXISTS LIKES CASCADE ;
DROP TABLE IF EXISTS MPA CASCADE ;
DROP TABLE IF EXISTS USERS CASCADE ;
DROP TABLE IF EXISTS DIRECTORS CASCADE ;
DROP TABLE IF EXISTS FILM_DIRECTORS CASCADE ;


create table IF NOT EXISTS USERS
(
    USER_ID       BIGINT auto_increment,
    USER_NAME     CHARACTER VARYING(100) not null,
    USER_EMAIL    CHARACTER VARYING(200) not null,
    USER_LOGIN    CHARACTER VARYING(50)  not null,
    USER_BIRTHDAY DATE                   not null,
    constraint USER_ID
        primary key (USER_ID)
);


create table IF NOT EXISTS MPA
(
    MPA_ID   INTEGER               not null,
    MPA_TYPE CHARACTER VARYING(10) not null,
    constraint MPA_ID
    primary key (MPA_ID)
);

create unique index IF NOT EXISTS MPA_ID_UINDEX
    on MPA (MPA_ID);

create table IF NOT EXISTS FILMS
(
    FILM_ID           BIGINT auto_increment,
    FILM_NAME         CHARACTER VARYING(100) not null,
    FILM_RELEASE_DATE DATE                   not null,
    FILM_DESCRIPTION  CHARACTER VARYING(200) not null,
    FILM_DURATION     INTEGER                not null,
    MPA_ID            INTEGER                not null,
    constraint FILM_ID
        primary key (FILM_ID),
    constraint MPA_ID_FK
        foreign key (MPA_ID) references MPA
);
create table IF NOT EXISTS LIKES
(
    USER_ID BIGINT not null,
    FILM_ID BIGINT not null,
    --constraint LIKES_PK
       --primary key (USER_ID) ,
    constraint LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
);

create table IF NOT EXISTS FRIENDS
(
    USER_ID       BIGINT  not null,
    FRIEND_ID     BIGINT  not null,
    FRIEND_STATUS BOOLEAN ,
    constraint FRIENDS_FK
        foreign key (FRIEND_ID) references USERS(USER_ID) ON DELETE CASCADE,
    constraint FRIENDS_FK_TWO
       foreign key (USER_ID) references USERS(USER_ID) ON DELETE CASCADE
);

create table IF NOT EXISTS GENRE_NAMES
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME CHARACTER VARYING(20) not null,
    constraint GENRE_NAMES_PK
    primary key (GENRE_ID)
    );

create table IF NOT EXISTS FILM_GENRES
(
    FILM_ID  BIGINT  not null,
    GENRE_ID INTEGER not null,
    constraint GENRES_FK
        foreign key (GENRE_ID) references GENRE_NAMES ON DELETE RESTRICT,
    constraint GENRES_FK_TWO
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE
);

create table IF NOT EXISTS DIRECTORS
(
    DIRECTOR_ID     INTEGER auto_increment,
    DIRECTOR_NAME   CHARACTER VARYING(100) not null,
    constraint DIRECTORS_PR
        primary key (DIRECTOR_ID)
);

create table IF NOT EXISTS FILM_DIRECTORS
(
    FILM_ID     BIGINT not null ,
    DIRECTOR_ID INTEGER not null ,

    constraint UNQ_FILM_DIRECTORS
        UNIQUE (FILM_ID, DIRECTOR_ID),
    constraint FILM_DIRECTORS_FK
        foreign key (DIRECTOR_ID) references DIRECTORS ON DELETE CASCADE,
    constraint FILM_DIRECTORS_FK_TWO
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE

);







