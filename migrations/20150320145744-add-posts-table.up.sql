create table posts (
       id serial primary key,
       title varchar(255) not null,
       url varchar(255),
       content text,
       user_id integer references users
);
