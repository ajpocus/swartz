(ns swartz.helpers
  (:require [cemerick.friend :as friend]
            [clojure.pprint :refer [pprint]])
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
