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

(def user-params {:username "foobar"
                  :password "password"})

(def post-params {:title "test post please ignore"
                  :url "https://i.imgur.com/R1raY.gif"
                  :content "Now back to your regularly scheduled programming."})

(def comment-params {:content "test comment please ignore"})

(defn create-test-user
  ([] (create-test-user user-params))
  ([params]
   (users/create<! test-db (:username params) (:password params))))

(defn create-test-post
  ([] (create-test-post post-params))
  ([params]
   (posts/create<! test-db
                   (:title params)
                   (:url params)
                   (:content params)
                   (:user-id params))))

(defn create-test-comment
  ([] (create-test-comment comment-params))
  ([params]
   (comments/create<! test-db
                      (:content params)
                      (:user-id params)
                      (:post-id params)
                      (:parent-id params))))
