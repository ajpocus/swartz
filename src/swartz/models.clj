(ns swartz.models
  (:require [yesql.core :refer [defqueries]]))

(def db {:classname "org.postgresql.Driver"
         :subprotocol "postgresql"
         :subname "//localhost:5432/swartz"})

(defqueries "swartz/sql/users.sql")
(defqueries "swartz/sql/posts.sql")
(defqueries "swartz/sql/comments.sql")
