create table users (
   id bigint not null,
    create_time timestamp,
    password varchar(255),
    username varchar(255),
    primary key (id)
)



create table orders (
   id bigint not null,
    user_id bigint,
    primary key (id)
)