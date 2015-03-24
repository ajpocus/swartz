(ns swartz.models.posts_test
  (:require (swartz.models [posts :as posts]
                           [users :as users]))
  (:use clojure.test
        swartz.test-commons))

(use-fixtures :each clean-db)

(deftest test-create-with-url
  (testing "Create a post with url only"
    (let [user (create-test-user)
          user-id (:id user)
          params (assoc post-params
                        :content nil
                        :user-id user-id)
          post (create-test-post params)]
      (is (= (:url params) (:url post))))))

(deftest test-create-with-content
  (testing "Create a post with content only"
    (let [user (create-test-user)
          user-id (:id user)
          params (assoc post-params
                        :url nil
                        :user-id user-id)
          post (create-test-post params)]
      (is (= (:content params) (:content post))))))

(deftest test-create-with-both-url-and-content
  (testing "Create a post with both content and url"
    (let [user (create-test-user)
          user-id (:id user)
          params (assoc post-params
                        :user-id user-id)
          post (create-test-post params)]
      (is (= (:url params) (:url post)))
      (is (= (:content params) (:content post))))))

(deftest test-create-with-no-url-or-content
  (testing "Try creating a post with no url or content"
    (let [user (create-test-user)
          user-id (:id user)
          params (assoc post-params
                        :url nil
                        :content nil
                        :user-id user-id)]
      (try
        (create-test-post params)
        (catch Exception e
          (is (= (class e) org.postgresql.util.PSQLException)))
        (finally
          (is (= 0 (count (posts/find-all test-db)))))))))

(deftest test-create-with-no-user-id
  (testing "Try creating a post with no user id"
    (try
      ;; post-params doesn't have user id by default -- it has to be assoc'd
      (create-test-post)
      (catch Exception e
        (is (= (class e) org.postgresql.util.PSQLException)))
      (finally
        (is (= 0 (count (posts/find-all test-db))))))))
