(ns swartz.controllers
  (:require [net.cgrand.enlive-html :as html]
            [ring.util.response :refer [redirect]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [clojure.pprint :refer [pprint]])
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

(html/defsnippet post-snippet "templates/post-snippet.html"
  [:.post]
  [post]
  [:a.title] (html/content (:title post))
  [:a.title] (fn [el]
               (let [url (:url post)]
                 ((html/content (:title post)) el)
                 (if (nil? url)
                   ((html/set-attr :href (str "/posts/" (:id post))) el)
                   ((html/set-attr :href url) el)))))

(html/defsnippet post-list "templates/post-list.html"
  [:.post-list]
  [{:keys [posts]}]
  [:.content] (html/content (map post-snippet posts)))

(html/defsnippet post-page "templates/show-post.html"
  [:.post]
  [post]
  [:a.title] (html/content (:title post))
  [:a.title] (fn [el]
               (let [url (:url post)]
                 (if (nil? url)
                   ((html/set-attr :href (str "/posts/" (:id post))) el)
                   ((html/set-attr :href url) el))))
  [:.content] (html/content (:content post))
  [:.new-comment :form.comment] (html/set-attr
                                 :action
                                 (str "/posts/" (:id post) "/comments"))
  [:.anti-forgery-field] (html/html-content (anti-forgery-field)))

(html/defsnippet new-post-form "templates/new-post-form.html"
  [:.new-post]
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

(defn get-posts [req]
  (let [posts (select posts)]
    (base-template
     {:content (post-list {:posts posts})})))

(defn new-post [req]
  (base-template {:content (new-post-form)}))

(defn create-post [req]
  (let [params (:params req)
        post (insert posts (values {:title (:title params)
                                    :url (if (empty? (:url params))
                                           nil
                                           (:url params))
                                    :content (:content params)}))]
    (redirect (str "/posts/" (:id post)))))

(defn get-post [req]
  (let [post-id (:id (:params req))
        post (first (select posts (where {:id (Integer/parseInt post-id)})))]
    (pprint post)
    (base-template
     {:content (post-page post)})))

(defn create-comment [req]
  (let [params (:params req)
        post-id (Integer/parseInt (:id params))
        post (first (select posts (where {:id post-id})))
        identity (friend/identity req)]
    (pprint identity)
    (insert comments (values {:content (:content params)
                              :post_id post-id
                              :user_id (:id identity)}))
    (redirect (str "/posts/" post-id))))
