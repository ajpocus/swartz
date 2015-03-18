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
  [:.notes] (content (str (count (:notes post)) (if (= (count (:notes post)) 1)
                                                  " note"
                                                  " notes"))))

(defsnippet post-list "templates/post-list.html"
  [:.post-list]
  [{:keys [posts identity]}]
  [:a.new-post] (if-show identity)
  [:ol.posts] (content (map (fn [post]
                              (post-snippet {:post post}))
                            posts)))

(defsnippet note-snippet "templates/note-snippet.html"
  [:.note]
  [{:keys [note]}]
  [:.content] (content (:content note))
  [:.user] (append (:username note))
  [:.reply] (set-attr :href (str "/posts/"
                                 (:post_id note)
                                 "/notes/"
                                 (:id note))))

(defsnippet note-list "templates/note-list.html"
  [:.note-list]
  [{:keys [notes]}]
  [:.note-list] (content (map (fn [note]
                                        (note-snippet {:note note}))
                                      notes)))

(defsnippet new-note-form "templates/new-note-form.html"
  [:form.note]
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
  [:.new-note] (do-> (if-show identity)
                     (content (new-note-form)))
  [:.new-note :form.note] (set-attr "action"
                                    (str "/posts/" (:id post) "/notes"))
  [:.no-note] (if-hide identity)
  [:.post :.notes] (append (note-list {:notes (:note post)}))
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet new-post-form "templates/new-post-form.html"
  [:.new-post]
  [{:keys [identity flash]}]
  [:.anti-forgery-field] (html-content (anti-forgery-field)))

(defsnippet show-note "templates/show-note.html"
  [:.note]
  [{:keys [identity post note]}]
  [:.post :.title] (content (:title post))
  [:.post :.user] (content (str "submitted by " (:username post)))
  [:.note :.content] (content (:content note))
  [:.note :.user] (content (:username note))
  [:.note :.timestamp] (content (time-elapsed (:created_on note)))
  [:.new-note] (do-> (if-show identity)
                     (content (new-note-form)))
  [:.notes] (content (map (fn [note]
                            (note-snippet {:note note}))
                          (:notes note))))
