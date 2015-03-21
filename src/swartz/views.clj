(ns swartz.views
  (:require [ring.util.anti-forgery :refer [anti-forgery-field]]
            [cemerick.friend :as friend])
  (:use net.cgrand.enlive-html
        swartz.helpers))

(defsnippet home-page "templates/home.html"
  [:.home]
  [{:keys [identity flash]}])

(defsnippet login-form "templates/login.html"
  [:.login]
  [{:keys [identity flash]}]
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet signup-form "templates/signup.html"
  [:.signup]
  [{:keys [identity flash]}]
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet post-snippet "templates/post-snippet.html"
  [:.post]
  [{:keys [post]}]
  [:a.title] (do-> (content (:title post))
                   (if-transform (:url post)
                                 (set-attr :href (:url post))
                                 (set-attr :href (str "/posts/" (:id post)))))
  [:.username] (content (:username post))
  [:.timestamp] (content (time-elapsed (:created_on post)))
  [:.comments] (do-> (set-attr :href (str "/posts/" (:id post)))
                  (content (comment-count post))))

(defsnippet post-list "templates/post-list.html"
  [:.post-list]
  [{:keys [posts identity]}]
  [:a.new-post] (if-show identity)
  [:ol.posts] (content (map (fn [post]
                              (post-snippet {:post post}))
                            posts)))

(defsnippet comment-snippet "templates/comment-snippet.html"
  [:.comment]
  [{:keys [comment]}]
  [:.content] (content (:content comment))
  [:.user] (append (:username comment))
  [:.reply] (set-attr :href (str "/posts/"
                                 (:post_id comment)
                                 "/comments/"
                                 (:id comment))))

(defsnippet comment-list "templates/comment-list.html"
  [:.comment-list]
  [{:keys [comments]}]
  [:.comment-list] (content (map (fn [comment]
                                        (comment-snippet {:comment comment}))
                                      comments)))

(defsnippet new-comment-form "templates/new-comment-form.html"
  [:form.comment]
  []
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet post-page "templates/show-post.html"
  [:.post]
  [{:keys [post identity]}]
  [:a.title] (content (:title post))
  [:a.title] (if-transform (:url post)
                           (set-attr :href (:url post))
                           (set-attr :href (str "/posts/" (:id post))))
  [:.content] (content (:content post))
  [:.new-comment] (do-> (if-show identity)
                     (content (new-comment-form)))
  [:.new-comment :form.comment] (set-attr :action
                                    (str "/posts/" (:id post) "/comments"))
  [:.no-comment] (if-hide identity)
  [:.post :.comments] (append (comment-list {:comments (:comment post)}))
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet new-post-form "templates/new-post-form.html"
  [:.new-post]
  [{:keys [identity flash]}]
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet show-comment "templates/show-comment.html"
  [:.comment]
  [{:keys [identity post comment]}]
  [:.post :.title] (content (:title post))
  [:.post :.user] (content (str "submitted by " (:username post)))
  [:.comment :.content] (content (:content comment))
  [:.comment :.user] (content (:username comment))
  [:.comment :.timestamp] (content (time-elapsed (:created_on comment)))
  [:.new-comment] (do-> (if-show identity)
                     (content (new-comment-form)))
  [:form.comment] (set-attr :action (str "/posts/" (:id post) "/comments"))
  [:form.comment [:input (attr= :name "parent_id")]] (set-attr :value
                                                               (:id comment))
  [:.comments] (content (map (fn [comment]
                            (comment-snippet {:comment comment}))
                          (:comments comment))))
