(ns swartz.helpers
  (:require [cemerick.friend :as friend]
            [clj-time.core :as t]
            [clj-time.coerce :refer [from-sql-time]])
  (:use net.cgrand.enlive-html))

(def not-nil? (complement nil?))

(defn if-show [pred]
  "If the predicate is truthy, show the node. Otherwise, hide it."
  (fn [node]
    (if pred
      ((remove-class "hidden") node)
      ((add-class "hidden") node))))

(defn if-hide [pred]
  "If the predicate is truthy, hide the node. Otherwise, show it."
  (fn [node]
    (if pred
      ((add-class "hidden") node)
      ((remove-class "hidden") node))))

(defn if-transform
  "If the predicate is truthy, run the given transform. Optionally takes a
  transform to be run if the predicate is falsey."
  ([pred trans] (fn [node]
                  (if pred
                    (trans node)
                    node)))
  ([pred t-trans nil-trans] (fn [node]
                              (if pred
                                (t-trans node)
                                (nil-trans node)))))

(deftemplate base-template "templates/base.html"
  [{:keys [page flash identity]}]
  [:#page] (content page)
  [:.flash] (content flash)
  [:.auth :.identity :.name] (if-transform identity
                               (content (:current identity)))
  [:.auth :.logout] (if-show identity)
  [:.auth :.login] (if-hide identity))

(defn wrap-view
  "Wraps an enlive snippet with a base template, and includes common params."
  ([req snippet-fn]
   (wrap-view req snippet-fn {}))
  ([req snippet-fn params]
   (let [flash (:flash req)
         identity (friend/identity req)]
     (base-template {:page (snippet-fn (assoc params
                                              :identity identity
                                              :flash flash))
                     :identity identity
                     :flash flash}))))

(defn time-elapsed [sql-timestamp]
  "Returns a string describing how long ago the timestamp is."
  (let [dt (from-sql-time sql-timestamp)
        now (t/now)
        interval (t/interval dt now)
        days (t/in-days interval)
        hours (t/in-hours interval)
        minutes (t/in-minutes interval)]
    (if (not (zero? days))
      (if (= days 1)
        "1 day ago"
        (str days " days ago"))
      (if (not (zero? hours))
        (if (= hours 1)
          "1 hour ago"
          (str hours " hours ago"))
        (if (not (zero? minutes))
          (if (= minutes 1)
            "1 minute ago"
            (str minutes " minutes ago"))
          "1 minute ago")))))

(defn comment-count [post]
  (let [num-comments (count (:comment post))]
    (str num-comments (if (= num-comments 1)
                     " comment"
                     " comments"))))

