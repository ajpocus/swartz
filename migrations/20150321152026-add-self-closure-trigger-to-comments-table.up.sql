create function add_self_closure() returns trigger as $add_self_closure$
       begin
        insert into comments_closure (parent_id, child_id, depth)
               values (new.id, new.id, 0);
           if (new.parent_id is not null) then
              insert into comments_closure (parent_id, child_id, depth)
                     select p.parent_id, c.child_id, p.depth+c.depth+1
                     from comments_closure p, comments_closure c
                     where p.child_id=new.parent_id and c.parent_id=new.id;
        end if;
        return null;
       end;
$add_self_closure$ language plpgsql;

create trigger aa_self_closure after insert on comments
       for each row execute procedure add_self_closure();
