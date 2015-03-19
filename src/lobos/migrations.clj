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

(defmigration add-comment-table
  (up [] (create
          (tbl :comment
               (text :content)
               (text :rank)
               (refer-to :user)
               (refer-to :post)
               (integer :parent_id
                        [:refer :comment :id :on-delete :set-null]))))
  (down [] (drop (table :comment))))

(defmigration add-comment-closure-table
  (up [] (create
          (table :comment_closure
                 (integer :parent_id
                          [:refer :comment :id :on-delete :set-null])
                 (integer :child_id
                          [:refer :comment :id :on-delete :set-null])
                 (integer :depth)
                 (primary-key [:parent_id :child_id]))))
  (down [] (drop (table :comment_closure))))
