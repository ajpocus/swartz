(ns swartz.models.comments-closure
  (:require [yesql.core :refer [defqueries]]))

(defqueries "swartz/sql/comments_closure.sql")
