drop table if exists users;
drop table if exists cities;

create table cities
(
    id         serial8,
    name       varchar not null,
    index      varchar not null,
    primary key (id)
);

create table users
(
    id               serial8,
    login            varchar  not null,
    name             varchar not null,
    city_id          int8 not null,
    unique (login),
    foreign key (city_id) references cities(id)
);
