(ns swartz.models.comments-test
  (:require (swartz.models [users :as users]
                           [posts :as posts]
                           [comments :as comments]
                           [comments-closure :as comments-closure]))
  (:use clojure.test
        swartz.test-commons))

(use-fixtures :each clean-db)

(deftest test-top-level-comment-creation
  (testing "Create a top-level comment"
    (let [user (create-test-user)
          post (create-test-post (assoc post-params :user-id (:id user)))
          params (assoc comment-params
                        :user-id (:id user)
                        :post-id (:id post))
          comment (create-test-comment params)]
      (is (= (:content comment) (:content comment-params)))
      (is (= 1 (count (comments-closure/find-by-triplet test-db
                                                        (:id comment)
                                                        (:id comment)
                                                        0)))))))

(deftest test-child-comment-creation
  (testing "Create a child comment"
    (let [user (create-test-user)
          post (create-test-post (assoc post-params :user-id (:id user)))
          parent-params (assoc comment-params
                               :user-id (:id user)
                               :post-id (:id post))
          parent-comment (create-test-comment parent-params)
          child-params (assoc parent-params
                              :parent-id (:id parent-comment))
          child-comment (create-test-comment child-params)]
      (is (= 1 (count (comments-closure/find-by-triplet test-db
                                                        (:id parent-comment)
                                                        (:id parent-comment)
                                                        0))))
      (is (= 1 (count (comments-closure/find-by-triplet test-db
                                                        (:id child-comment)
                                                        (:id child-comment)
                                                        0))))
      (is (= 1 (count (comments-closure/find-by-triplet test-db
                                                        (:id parent-comment)
                                                        (:id child-comment)
                                                        1)))))))

;; The comment hierarchy here is from this article describing closure tables:
;; https://coderwall.com/p/lixing/closure-tables-for-browsing-trees-in-sql
;; Here's a visual representation that should make it a bit more clear:
;; 1
;; | - 2
;;     | - 3
;;         | - 6
;;     | - 5
;; | - 4

(deftest test-find-by-post
  (testing "Find all comments for a given post"
    (let [user (create-test-user)
          post (create-test-post (assoc post-params :user-id (:id user)))
          comment1-params (assoc comment-params
                                 :user-id (:id user)
                                 :post-id (:id post))
          comment1 (create-test-comment comment1-params)
          comment2-params (assoc comment1-params
                                 :parent-id (:id comment1))
          comment2 (create-test-comment comment2-params)
          comment3-params (assoc comment1-params
                                 :parent-id (:id comment2))
          comment3 (create-test-comment comment3-params)
          comment4-params (assoc comment1-params
                                 :parent-id (:id comment1))
          comment4 (create-test-comment comment4-params)
          comment5-params (assoc comment1-params
                                 :parent-id (:id comment2))
          comment5 (create-test-comment comment5-params)
          comment6-params (assoc comment1-params
                                 :parent-id (:id comment3))
          comment6 (create-test-comment comment6-params)
          post-comments (comments/find-by-post test-db (:id post))]
      (is (= 6 (count post-comments)))
      (is (= (:id comment6) (:id (nth post-comments 3))))
      (is (= (:id comment5) (:id (nth post-comments 4)))))))
