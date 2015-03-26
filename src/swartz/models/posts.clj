(ns swartz.models.posts
  (:require [yesql.core :refer [defqueries]]))

(defqueries "swartz/sql/posts.sql")
