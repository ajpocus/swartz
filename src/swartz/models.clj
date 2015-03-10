(ns swartz.models
  (:use korma.db
        korma.core))

(defdb db (postgres {:db "swartz"
                     :user "austin"
                     :password "notasecurepass"}))

(declare users posts comments)

(defentity users
  (has-many posts))

(defentity posts
  (belongs-to users)
  (has-many comments))

(defentity comments
  (belongs-to users)
  (belongs-to posts)
  (belongs-to comments)
  (has-many comments))
