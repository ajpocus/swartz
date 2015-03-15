(ns swartz.helpers
  (:require [cemerick.friend :as friend])
  (:use net.cgrand.enlive-html))

(defmacro wrap-view [req snippet]
  "Wraps an enlive snippet with a base template, and includes common params."
  (let [identity (friend/identity req)
        flash (:flash req)
        base-params (or (second snippet) {})
        params (assoc base-params
                      :identity (friend/identity req)
                      :flash flash)]
    `(base-template {:page (~(first snippet) ~params)
                           :identity ~identity
                           :flash ~flash})))

(defn if-show [pred]
  "If the predicate is truthy, show the el. Otherwise, hide it."
  (fn [node]
    (if pred
      (at node (remove-class "hidden"))
      (at node (add-class "hidden")))))

(defn if-hide [pred]
  "If the predicate is truthy, hide the el. Otherwise, show it."
  (fn [node]
    (if pred
      (at node (add-class "hidden"))
      (at node (remove-class "hidden")))))

(defn if-transform
  "If the predicate is truthy, run the given transform. Optionally takes a
  transform to be run if the predicate is falsey."
  ([pred trans] (fn [el]
                  (if pred
                    (trans el)
                    el)))
  ([pred t-trans nil-trans] (fn [el]
                              (if pred
                                (t-trans el)
                                (nil-trans el)))))

(deftemplate base-template "templates/base.html"
  [{:keys [page flash identity]}]
  [:#page] (content page)
  [:.flash] (content flash)
  [:.auth :.identity :.name] (if-transform identity
                               (content (:current identity)))
  [:.auth :.logout] (if-show identity)
  [:.auth :.login] (if-hide identity))
