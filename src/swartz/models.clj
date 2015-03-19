(ns swartz.models
  (:use korma.db
        korma.core))

(defdb db (postgres {:db "swartz"
                     :user "austin"
                     :password "notasecurepass"}))

(declare user post comment comment-closure)

(defentity user
  (has-many post))

(defentity post
  (belongs-to user)
  (has-many comment))

(defentity comment
  (belongs-to user)
  (belongs-to post)
  (belongs-to comment {:fk :parent_id})
  (has-many comment {:fk :parent_id}))

(defentity comment-closure
  (table :comment_closure))

(defn create-comment [attrs]
  (let [ent (insert comment (values attrs))
        id (:id ent)
        parent-id (:parent_id ent)]
    (insert comment-closure
            (values {:parent_id id
                     :child_id id
                     :depth 0}))
    (when parent-id
      (exec-raw
       ["insert into comment_closure (parent_id, child_id, depth)
                    select p.parent_id, c.child_id, p.depth+c.depth+1
                      from comment_closure p, comment_closure c
                    where p.child_id=? and c.parent_id=?" [parent-id id]]))))

(defn get-comment-tree [post]
  (select comment
          (join comment-closure (= :comment_closure.child_id :id))
          (where {:post_id (:id post)})))
