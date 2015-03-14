(ns swartz.controllers
  (:require [ring.util.response :refer [redirect]]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [swartz.views :as views])
  (:use korma.core
        swartz.models))

(defn- create-user [{:keys [username password]}]
  {:identity username
   :password password
   :roles #{::user}})

(defn get-homepage [req]
  (views/base-template {:page (views/home-page)
                        :identity (friend/identity req)}))

(defn get-login [req]
  (views/base-template {:page (views/login-form)}))

(defn get-signup [req]
  (views/base-template {:page (views/signup-form)
                        :flash (:flash req)}))

(defn post-signup [req]
  (let [{:keys [username password]} (:params req)]
    (if (> (:count (first (select users
                                  (aggregate (count :*) :count)
                                  (where {:username username}))))
           0)
      (assoc-in (redirect "/signup")
                [:session :_flash]
                "That username is taken.")
      (do
        (let [user (insert users
                           (values {:username username
                                    :password (creds/hash-bcrypt password)}))]
          (friend/merge-authentication (redirect "/") (create-user user)))))))

(defn get-posts [req]
  (let [posts (select posts)]
    (views/base-template
     {:page (views/post-list {:posts posts})
      :identity (friend/identity req)})))

(defn new-post [req]
  (views/base-template {:page (views/new-post-form)}))

(defn create-post [req]
  (let [params (:params req)
        post (insert posts (values {:title (:title params)
                                    :url (if (empty? (:url params))
                                           nil
                                           (:url params))
                                    :content (:content params)}))]
    (redirect (str "/posts/" (:id post)))))

(defn get-post [req]
  (let [post-id (Integer/parseInt (:id (:params req)))
        post (first (select posts
                            (with comments)
                            (where {:id post-id})))
        identity (friend/identity req)]
    (views/base-template
     {:page (views/post-page {:post post
                              :identity identity})
      :identity identity})))

(defn create-comment [req]
  (let [params (:params req)
        post-id (Integer/parseInt (:id params))
        post (first (select posts (where {:id post-id})))
        identity (friend/identity req)
        user (first (select users (where {:username (:current identity)})))]
    (insert comments (values {:content (:content params)
                              :post_id post-id
                              :user_id (:id user)}))
    (redirect (str "/posts/" post-id))))
