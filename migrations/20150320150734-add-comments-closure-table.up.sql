create table comments_closure (
       parent_id integer references comments,
       child_id integer references comments,
       depth integer,
       primary key (parent_id, child_id)
);
