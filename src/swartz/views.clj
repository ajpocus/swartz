(ns swartz.views
  (:require [ring.util.anti-forgery :refer [anti-forgery-field]]
            [cemerick.friend :as friend])
  (:use net.cgrand.enlive-html))

(deftemplate base-template "templates/base.html"
  [{:keys [page flash identity]}]
  [:#page] (content page)
  [:.flash] (content flash)
  [:.auth :.identity :.name] (fn [el]
                               (if (nil? identity)
                                 el
                                 ((content (:current identity)) el)))
  [:.auth :.logout] (fn [el]
                      (if (nil? identity)
                        el
                        ((remove-class "hidden") el)))
  [:.auth :.login] (fn [el]
                     (if (nil? identity)
                       el
                       ((add-class "hidden") el))))

(defsnippet home-page "templates/home.html"
  [:.home]
  [])

(defsnippet login-form "templates/login.html"
  [:.login]
  []
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet signup-form "templates/signup.html"
  [:.signup]
  []
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet post-snippet "templates/post-snippet.html"
  [:.post]
  [{:keys [post]}]
  [:a.title] (content (:title post))
  [:a.title] (fn [el]
               (let [url (:url post)]
                 ((content (:title post)) el)
                 (if (nil? url)
                   ((set-attr :href (str "/posts/" (:id post))) el)
                   ((set-attr :href url) el)))))

(defsnippet post-list "templates/post-list.html"
  [:.post-list]
  [{:keys [posts identity]}]
  [:a.new-post] (fn [el]
                  (if (nil? identity)
                    el
                    ((remove-class "hidden") el)))
  [:ol.posts] (content (map (fn [post]
                              (post-snippet {:post post}))
                            posts)))

(defsnippet note-snippet "templates/note-snippet.html"
  [:.note]
  [{:keys [note]}]
  [:.content] (content (:content note))
  [:.user] (append (:username note)))

(defsnippet note-list "templates/note-list.html"
  [:.note-list]
  [{:keys [notes]}]
  [:.note-list] (content (map (fn [note]
                                        (note-snippet {:note note}))
                                      notes)))

(defsnippet post-page "templates/show-post.html"
  [:.post]
  [{:keys [post identity]}]
  [:a.title] (content (:title post))
  [:a.title] (fn [el]
               (let [url (:url post)]
                 (if (nil? url)
                   ((set-attr :href (str "/posts/" (:id post))) el)
                   ((set-attr :href url) el))))
  [:.content] (content (:content post))
  [:.new-note :form.note] (set-attr
                                 :action
                                 (str "/posts/" (:id post) "/notes"))
  [:.new-note] (fn [el]
                    (if (nil? identity)
                      ((add-class "hidden") el)
                      el))
  [:.no-note] (fn [el]
                   (if (nil? identity)
                     ((remove-class "hidden") el)
                     el))
  [:.post :.notes] (append (note-list {:notes (:note post)}))
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet new-post-form "templates/new-post-form.html"
  [:.new-post]
  []
  [:.anti-forgery-field] (html-content (anti-forgery-field)))
