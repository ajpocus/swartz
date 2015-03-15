(ns lobos.migrations
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]] core schema
               config helpers)))

(defmigration add-user-table
  (up [] (create
          (tbl :user
               (varchar :username 255 :unique)
               (varchar :password 255))))
  (down [] (drop (table :user))))

(defmigration add-post-table
  (up [] (create
          (tbl :post
               (varchar :title 255)
               (varchar :url 255)
               (text :content)
               (refer-to :user))))
  (down [] (drop (table :post))))

(defmigration add-note-table
  (up [] (create
          (tbl :note
               (text :content)
               (refer-to :user)
               (refer-to :post)
               (refer-to :note))))
  (down [] (drop (table :note))))
