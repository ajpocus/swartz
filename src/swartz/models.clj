(ns swartz.models
  (:use korma.db
        korma.core))

(defdb db (postgres {:db "swartz"
                     :user "austin"
                     :password "notasecurepass"}))

(declare user post comment)

(defentity user
  (has-many post))

(defentity post
  (belongs-to user)
  (has-many comment))

(defentity comment
  (belongs-to user)
  (belongs-to post)
  (belongs-to comment)
  (has-many comment))
