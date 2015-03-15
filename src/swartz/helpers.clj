(ns swartz.helpers
  (:require [cemerick.friend :as friend]
            [swartz.views :as views])
  (:use net.cgrand.enlive-html))

(defmacro wrap-view [req snippet]
  (let [identity (friend/identity req)
        flash (:flash req)
        base-params (or (second snippet) {})
        params (assoc base-params
                      :identity (friend/identity req)
                      :flash flash)]
    `(views/base-template {:page (~(first snippet) ~params)
                           :identity ~identity
                           :flash ~flash})))
