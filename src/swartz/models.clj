(ns swartz.models
  (:use korma.db
        korma.core))

(defdb db (postgres {:db "swartz"
                     :user "austin"
                     :password "notasecurepass"}))

(declare users posts comments)

(defentity users
  (has-many posts {:fk :user_id}))

(defentity posts
  (belongs-to users {:fk :user_id})
  (has-many comments {:fk :post_id}))

(defentity comments
  (belongs-to users {:fk :user_id})
  (belongs-to posts {:fk :post_id})
  (belongs-to comments {:fk :comment_id})
  (has-many comments {:fk :comment_id}))
