(ns swartz.models.users_test
  (:use clojure.test)
  (:require [swartz.models.users :as users]))

(def test-db {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "//localhost:5432/swartz_test"})

(defn- clean-db [f]
  (users/delete-all! test-db)
  (f))

(use-fixtures :each clean-db)

(deftest test-create
  (let [username "foobar"
        password "password"
        user (users/create<! test-db username password)]
    (is (= username (:username (first (users/find-all test-db)))))))

(deftest test-find-all
  (let [user1 (users/create<! test-db "foobar" "password")
        user2 (users/create<! test-db "bazquux" "1234")]
    (is (= 2 (count (users/find-all test-db))))))

(deftest test-find-by-username
  (let [username "foo"
        password "pass"]
    (users/create<! test-db username password)
    (is (= 1 (count (users/find-by-username test-db username))))))

(deftest test-delete-all
  (users/create<! test-db "asdf" "qwerty")
  (users/delete-all! test-db)
  (is (= 0 (count (users/find-all test-db)))))
