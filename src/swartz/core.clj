(ns swartz.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [compojure.core :refer [defroutes GET]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [resources not-found]]
            [swartz.controllers :as ctrl])
  (:gen-class))

(defroutes app-routes
  (GET "/" req (ctrl/get-homepage req))
  (resources "/")
  (not-found "Page not found."))

(def handler
  (-> app-routes
      (wrap-defaults site-defaults)
      (site)))

(defn -main
  "Start a ring/jetty server."
  [& args]
  (run-jetty handler {:port 3000}))
