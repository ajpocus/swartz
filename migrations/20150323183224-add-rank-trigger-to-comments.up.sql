create function add_rank() returns trigger as $add_rank$
       begin
        if new.parent_id is null then
           new.rank := lpad(new.id::text, 10, '0');
        else
           new.rank := concat(
                        (select rank from comments where id = new.parent_id),
                        '-',
                        lpad(new.id::text, 10, '0'));
        end if;
        return new;
       end;
$add_rank$ language plpgsql;

create trigger ab_add_rank before insert on comments
       for each row execute procedure add_rank();
