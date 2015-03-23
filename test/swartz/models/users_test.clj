(ns swartz.models.users_test
  (:require [swartz.models.users :as users])
  (:use clojure.test
        swartz.test-commons))

(use-fixtures :each clean-db)

(deftest test-create-with-username-and-password
  (testing "Create a user with username and password"
    (create-test-user)
    (is (= (:username user-params)
           (:username (first (users/find-all test-db)))))))

(deftest test-create-with-duplicate-username
  (testing "Try creating a user with a duplicate username"
    (create-test-user)
    (try
      (create-test-user)
      (catch Exception e
        (is (= (class e) org.postgresql.util.PSQLException)))
      (finally
        (is (= 1 (count (users/find-all test-db))))))))

(deftest test-create-with-no-username
  (testing "Try creating a user with no username"
    (try
      (create-test-user (assoc user-params :username nil))
      (catch Exception e
        (is (= (class e) org.postgresql.util.PSQLException)))
      (finally
        (is (empty? (users/find-all test-db)))))))

(deftest test-find-all
  (testing "Finding all users"
    (let [user1 (create-test-user)
          user2 (create-test-user (assoc user-params :username "bazquux"))]
      (is (= 2 (count (users/find-all test-db)))))))

(deftest test-find-by-username
  (testing "Find a user by username"
    (let [username "foo"
          password "pass"]
      (users/create<! test-db username password)
      (is (= 1 (count (users/find-by-username test-db username)))))))

(deftest test-delete-all
  (testing "Delete all users"
    (create-test-user)
    (users/delete-all! test-db)
    (is (= 0 (count (users/find-all test-db))))))
