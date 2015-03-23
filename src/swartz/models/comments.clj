(ns swartz.models.comments
  (:require [yesql.core :refer [defqueries]]))

(def db {:classname "org.postgresql.Driver"
         :subprotocol "postgresql"
         :subname "//localhost:5432/swartz"})

(defqueries "swartz/sql/comments.sql")