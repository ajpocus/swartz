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
  transform to be run if the predicate is falsy."
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
  [:.auth :.identity :.name] (when identity
                               (content (:current identity)))
  [:.auth :.logout] (if-show identity)
  [:.auth :.login] (if-hide identity))
