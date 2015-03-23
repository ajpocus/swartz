alter table posts add constraint chk_post_readables
      check (url is not null or content is not null);
