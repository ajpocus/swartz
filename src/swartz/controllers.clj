(ns swartz.controllers
  (:require [net.cgrand.enlive-html :as html]))

(html/deftemplate base-template "templates/base.html"
  [{:keys [content]}]
  [:#page] (html/content content))

(html/defsnippet home-page "templates/home.html"
  [:.home]
  [])

(defn get-homepage [req]
  (base-template {:content (home-page)}))
