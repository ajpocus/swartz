(ns swartz.controllers
  (:require [ring.util.response :refer [redirect]]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [clojure.tools.logging :as log]
            [clojure.pprint :refer [pprint]]
            [swartz.views :as views])
  (:use korma.core
        swartz.models
        swartz.helpers))

(defn- create-user [{:keys [username password]}]
  {:identity username
   :password password
   :roles #{::user}})

(defn get-homepage [req]
  (log/info (friend/identity req))
  (wrap-view req views/home-page))

(defn get-login [req]
  (wrap-view req views/login-form))

(defn get-signup [req]
  (wrap-view req views/signup-form))

(defn post-signup [req]
  (let [{:keys [username password]} (:params req)]
    (if (> (:count (first (select user
                                  (aggregate (count :*) :count)
                                  (where {:username username}))))
           0)
      (assoc-in (redirect "/signup")
                [:session :_flash]
                "That username is taken.")
      (let [user (insert user
                         (values {:username username
                                  :password (creds/hash-bcrypt password)}))]
        (friend/merge-authentication (redirect "/") (create-user user))))))

(defn get-posts [req]
  (let [posts (select post
                      (with user (fields :username))
                      (with comment))
        identity (friend/identity req)]
    (wrap-view req views/post-list {:posts posts})))

(defn new-post [req]
  (wrap-view req views/new-post-form))

(defn create-post [req]
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

(defn create-comment [req]
  (let [params (:params req)
        post-id (Integer/parseInt (:post_id params))
        comment-id (if (empty? (:comment_id params))
                  nil
                  (Integer/parseInt (:comment_id params)))
        post (first (select post (where {:id post-id})))
        identity (friend/identity req)
        user (first (select user (where {:username (:current identity)})))]
    (insert comment (values {:content (:content params)
                          :post_id post-id
                          :comment_id comment-id
                          :user_id (:id user)}))
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
