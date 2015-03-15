(ns swartz.models
  (:use korma.db
        korma.core))

(defdb db (postgres {:db "swartz"
                     :user "austin"
                     :password "notasecurepass"}))

(declare user post note)

(defentity user
  (has-many post))

(defentity post
  (belongs-to user)
  (has-many note))

(defentity note
  (belongs-to user)
  (belongs-to post)
  (belongs-to note)
  (has-many note))
