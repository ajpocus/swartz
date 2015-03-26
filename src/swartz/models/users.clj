(ns swartz.models.users
  (:require [yesql.core :refer [defqueries]]))

(defqueries "swartz/sql/users.sql")
