(ns swartz.controllers
  (:require [net.cgrand.enlive-html :as html]
            [ring.util.response :refer [redirect]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds])
  (:use korma.core
        swartz.models))

(html/deftemplate base-template "templates/base.html"
  [{:keys [content flash identity]}]
  [:#page] (html/content content)
  [:.flash] (html/content flash)
  [:.auth :.identity :.name] (fn [el]
                               (if (nil? identity)
                                 el
                                 ((html/content (:current identity)) el)))
  [:.auth :.logout] (fn [el]
                      (if (nil? identity)
                        el
                        ((html/remove-class "hidden") el)))
  [:.auth :.login] (fn [el]
                     (if (nil? identity)
                       el
                       ((html/add-class "hidden") el))))

(html/defsnippet home-page "templates/home.html"
  [:.home]
  [])

(html/defsnippet login-form "templates/login.html"
  [:.login]
  []
  [:.anti-forgery-field] (html/html-content (anti-forgery-field)))

(html/defsnippet signup-form "templates/signup.html"
  [:.signup]
  []
  [:.anti-forgery-field] (html/html-content (anti-forgery-field)))

(defn- create-user [{:keys [username password]}]
  {:identity username
   :password password
   :roles #{::user}})

(defn get-homepage [req]
  (base-template {:content (home-page)
                  :identity (friend/identity req)}))

(defn get-login [req]
  (base-template {:content (login-form)}))

(defn get-signup [req]
  (base-template {:content (signup-form)
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
