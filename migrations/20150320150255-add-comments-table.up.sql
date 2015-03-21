create table comments (
       id serial primary key,
       content text not null,
       user_id integer references users,
       post_id integer references posts,
       parent_id integer references comments
);
