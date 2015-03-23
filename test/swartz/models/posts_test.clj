(ns swartz.models.posts_test
  (:require (swartz.models [posts :as posts]
                           [users :as users]))
  (:use clojure.test
        swartz.test-commons))

(use-fixtures :each clean-db)

(deftest test-create
  (let [user (first (users/find-all test-db))
        user-id (:id user)]
    (testing "Create with URL"
      (let [params (assoc post-params
                          :content nil
                          :user-id user-id)
            post (create-test-post params)]
        (is (= (:url params) (:url post)))))
    (testing "Create with content"
      (let [params (assoc post-params
                          :url nil
                          :user-id user-id)
            post (create-test-post params)]
        (is (= (:content params) (:content post)))))
    (testing "Create with no content or URL"
      (let [params (assoc post-params
                          :url nil
                          :content nil
                          :user-id user-id)]
        (try
          (create-test-post params)
          (catch Exception e
            (is (= (class e) org.postgresql.util.PSQLException)))
          (finally
            (is (= 0 (count (posts/find-all test-db))))))))
    (testing "Create with no user id"
      (try
        ;; post-params doesn't have user id by default -- it has to be assoc'd
        (create-test-post)
        (catch Exception e
          (is (= (class e) org.postgresql.util.PSQLException)))
        (finally
          (is (= 0 (count (posts/find-all test-db)))))))))
