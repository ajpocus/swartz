(ns swartz.test-commons
  (:require (swartz.models [users :as users]
                           [posts :as posts]
                           [comments :as comments]))
  (:use clojure.test))

(def test-db {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "//localhost:5432/swartz_test"})

(defn clean-db [f]
  (comments/delete-all! test-db)
  (posts/delete-all! test-db)
  (users/delete-all! test-db)
  (f))

(defn create-test-user [f]
  (users/create<! test-db "foobar" "password")
  (f))
