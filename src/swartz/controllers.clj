(ns swartz.controllers
  (:require [ring.util.response :refer [redirect]]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [clojure.tools.logging :as log]
            [clojure.pprint :refer [pprint]]
            [swartz.views :as views]
            (swartz.models [users :as users]
                           [posts :as posts]
                           [comments :as comments]))
  (:use swartz.helpers))

(defn- user-map [{:keys [username password]}]
  {:identity username
   :password password
   :roles #{::user}})

(defn get-login [req]
  (let [params (:params req)
        failed (not (empty? (:login_failed params)))]
    (wrap-view req views/login-form {:failed failed
                                     :username (:username params)})))

(defn get-signup [req]
  (wrap-view req views/signup-form))

(defn post-signup [req]
  (let [{:keys [username password]} (:params req)]
    (if (empty? username)
      (assoc-in (redirect "/signup")
                [:session :_flash]
                "Please enter a username.")
      (if (empty? password)
        (assoc-in (redirect "/signup")
                  [:session :_flash]
                  "Please enter a password.")
        (if (> (count (users/find-by-username users/db username)) 0)
          (assoc-in (redirect "/signup")
                    [:session :_flash]
                    "That username is taken.")
          (let [user (users/create<! users/db
                                     username
                                     (creds/hash-bcrypt password))]
            (friend/merge-authentication (redirect "/") (user-map user))))))))

(defn get-posts [req]
  (let [posts (posts/find-all posts/db)
        identity (friend/identity req)]
    (wrap-view req views/post-list {:posts posts})))

(defn get-post-form [req]
  (wrap-view req views/new-post-form))

(defn post-post [req]
  (let [params (:params req)
        identity (friend/identity req)
        user (first (users/find-by-username users/db (:current identity)))
        title (:title params)
        url (:url params)
        content (:content params)
        post (posts/create<! posts/db
                             (:title params)
                             (:url params)
                             (:content params)
                             (:id user))]
    (redirect (str "/posts/" (:id post)))))

(defn get-post [req]
  (let [post-id (Integer/parseInt (:id (:params req)))
        post (first (posts/find-by-id posts/db post-id))
        comment-list (comments/find-by-post comments/db post-id)
        identity (friend/identity req)]
    (wrap-view req views/post-page {:post post :comments comment-list})))

(defn post-comment [req]
  (let [params (:params req)
        post-id (Integer/parseInt (:post_id params))
        parent-id (if (empty? (:parent_id params))
                    nil
                    (Integer/parseInt (:parent_id params)))
        post (first (posts/find-by-id posts/db post-id))
        identity (friend/identity req)
        user (first (users/find-by-username users/db (:current identity)))]
    (comments/create<! comments/db
                      (:content params)
                      (:id user)
                      post-id
                      parent-id)
    (redirect (str "/posts/" post-id))))

(defn get-comment [req]
  (let [params (:params req)
        post-id (Integer/parseInt (:post_id params))
        comment-id (Integer/parseInt (:comment_id params))
        post (first (posts/find-by-id posts/db post-id))
        comment (first (comments/find-by-id comments/db comment-id))]
    (wrap-view req views/show-comment {:post post :comment comment})))
