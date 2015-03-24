alter table posts add constraint chk_user_id
      check (user_id is not null);
