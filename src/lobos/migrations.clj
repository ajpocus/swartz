(ns lobos.migrations
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]] core schema
               config helpers)))

(defmigration add-users-table
  (up [] (create
          (tbl :users
               (varchar :username 255 :unique)
               (varchar :password 255))))
  (down [] (drop (table :users))))

(defmigration add-posts-table
  (up [] (create
          (tbl :posts
               (varchar :title 255)
               (varchar :url 255)
               (text :content)
               (refer-to :users))))
  (down [] (drop (table :posts))))

(defmigration add-comments-table
  (up [] (create
          (tbl :comments
               (text :content)
               (refer-to :posts)
               (refer-to :comments))))
  (down [] (drop (table :comments))))
