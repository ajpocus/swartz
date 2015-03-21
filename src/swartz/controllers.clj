(ns swartz.controllers
  (:require [ring.util.response :refer [redirect]]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [clojure.tools.logging :as log]
            [clojure.pprint :refer [pprint]]
            [swartz.views :as views])
  (:use swartz.models
        swartz.helpers))

(defn- user-map [{:keys [username password]}]
  {:identity username
   :password password
   :roles #{::user}})

(defn get-homepage [req]
  (wrap-view req views/home-page))

(defn get-login [req]
  (wrap-view req views/login-form))

(defn get-signup [req]
  (wrap-view req views/signup-form))

(defn post-signup [req]
  (let [{:keys [username password]} (:params req)]
    (if (> (count (find-user-by-username username)) 0)
      (assoc-in (redirect "/signup")
                [:session :_flash]
                "That username is taken.")
      (let [user (create-user username (creds/hash-bcrypt password))]
        (friend/merge-authentication (redirect "/") (user-map user))))))

(defn get-posts [req]
  (let [posts (all-posts)
        identity (friend/identity req)]
    (wrap-view req views/post-list {:posts posts})))

(defn get-post-form [req]
  (wrap-view req views/new-post-form))

(defn post-post [req]
  (let [params (:params req)
        identity (friend/identity req)
        user (first (select user (where {:username (:current identity)})))
        post (insert post (values {:title (:title params)
                                   :url (if (empty? (:url params))
                                          nil
                                          (:url params))
                                   :content (:content params)
                                   :user_id (:id user)}))]
    (redirect (str "/posts/" (:id post)))))

(defn get-post [req]
  (let [post-id (Integer/parseInt (:id (:params req)))
        post (first (select post
                            (with comment
                                  (with user (fields :username)))
                            (where {:id post-id})))
        identity (friend/identity req)]
    (wrap-view req views/post-page {:post post})))

(defn post-comment [req]
  (let [params (:params req)
        post-id (Integer/parseInt (:post_id params))
        comment-id (if (empty? (:comment_id params))
                  nil
                  (Integer/parseInt (:comment_id params)))
        post (first (select post (where {:id post-id})))
        identity (friend/identity req)
        user (first (select user (where {:username (:current identity)})))]
    (create-comment {:content (:content params)
                     :post_id post-id
                     :user_id (:id user)
                     :parent_id comment-id})
    (redirect (str "/posts/" post-id))))

(defn get-comment [req]
  (let [params (:params req)
        post-id (Integer/parseInt (:post_id params))
        comment-id (Integer/parseInt (:comment_id params))
        post (first (select post
                            (with user (fields :username))
                            (where {:id post-id})))
        comment (first (select comment
                            (with user (fields :username))
                            (where {:id comment-id})))]
    (wrap-view req views/show-comment {:post post :comment comment})))
