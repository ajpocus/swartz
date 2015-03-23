(ns swartz.models.posts_test
  (:require (swartz.models [posts :as posts]
                           [users :as users]))
  (:use clojure.test))

(def test-db {:classname "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname "//localhost:5432/swartz_test"})

(defn- clean-db [f]
  (users/delete-all! test-db)
  (f))

(defn- create-user [f]
  (users/create<! test-db "foobar" "password")
  (f))

(use-fixtures :each clean-db)

(def params {:title "test post please ignore"
             :url "https://i.imgur.com/R1raY.gif"
             :content "We now return to your regularly scheduled programming."})

(deftest test-create
  (let [user (first (users/find-all test-db))
        user-id (:id user)]
    (testing "Create with URL"
      (let [post (posts/create<! test-db
                                 (:title params)
                                 (:url params)
                                 nil
                                 user-id)]
        (is (= (:url params) (:url post)))))
    (testing "Create with content"
      (let [post (posts/create<! test-db
                                 (:title params)
                                 nil
                                 (:content params)
                                 user-id)]
        (is (= (:content params) (:content post)))))
    (testing "Create with no content or URL"
      (try
        (posts/create<! test-db
                        (:title params)
                        nil
                        nil
                        user-id)
        (catch Exception e
          (is (= (.getClass e) "PSQLException")))
        (finally
          (is (= 0 (count (posts/find-all test-db)))))))
    (testing "Create with no user id"
      (try
        (posts/create<! test-db
                        (:title params)
                        (:url params)
                        nil
                        nil)
        (catch Exception e
          (is (= (.getClass e) "PSQLException")))
        (finally
          (is (= 0 (count (posts/find-all test-db)))))))))
