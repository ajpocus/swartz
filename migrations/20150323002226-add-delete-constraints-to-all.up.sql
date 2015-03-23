alter table posts
      drop constraint posts_user_id_fkey,
      add constraint posts_user_id_fkey
          foreign key (user_id)
          references users(id)
          on delete restrict;

alter table comments
      drop constraint comments_user_id_fkey,
      drop constraint comments_post_id_fkey,
      drop constraint comments_parent_id_fkey,
      add constraint comments_user_id_fkey
          foreign key (user_id)
          references users(id)
          on delete restrict,
      add constraint comments_post_id_fkey
          foreign key (post_id)
          references posts(id)
          on delete cascade,
      add constraint comments_parent_id_fkey
          foreign key (parent_id)
          references comments(id)
          on delete restrict;

alter table comments_closure
      drop constraint comments_closure_parent_id_fkey,
      drop constraint comments_closure_child_id_fkey,
      add constraint comments_closure_parent_id_fkey
          foreign key (parent_id)
          references comments(id)
          on delete cascade,
      add constraint comments_closure_child_id_fkey
          foreign key (child_id)
          references comments(id)
          on delete cascade;
