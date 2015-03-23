(ns swartz.models.comments-test
  (:require (swartz.models [users :as users]
                           [posts :as posts]
                           [comments :as comments]
                           [comments-closure :as comments-closure]))
  (:use clojure.test
        swartz.test-commons))

(use-fixtures :each clean-db)

(deftest test-create
  (let [user (create-test-user)
        post (create-test-post (assoc post-params :user-id (:id user)))]
    (testing "Create top-level comment"
      (let [params (assoc comment-params
                          :user-id (:id user)
                          :post-id (:id post))
            comment (create-test-comment params)]
        (is (= (:content comment) (:content comment-params)))
        (is (= 1 (count (comments-closure/find-by-triplet test-db
                                                          (:id comment)
                                                          (:id comment)
                                                          0))))))

    (testing "Create second-level comment"
      (let [top-params (assoc comment-params
                              :user-id (:id user)
                              :post-id (:id post))
            top-comment (create-test-comment top-params)
            second-params (assoc top-params
                                 :parent-id (:id top-comment))
            second-comment (create-test-comment second-params)]
        (is (= (:content second-comment) (:content comment-params)))
        (is (= 1 (count (comments-closure/find-by-triplet test-db
                                                          (:id top-comment)
                                                          (:id top-comment)
                                                          0))))
        (is (= 1 (count (comments-closure/find-by-triplet test-db
                                                          (:id second-comment)
                                                          (:id second-comment)
                                                          0))))
        (is (= 1 (count (comments-closure/find-by-triplet test-db
                                                          (:id top-comment)
                                                          (:id second-comment)
                                                          1))))))))
