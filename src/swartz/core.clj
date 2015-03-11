(ns swartz.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :refer [redirect]]
            (compojure [core :refer [defroutes GET POST]]
                       [handler :refer [site]]
                       [route :refer [resources not-found]])
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])
            [swartz.controllers :as ctrl])
  (:use korma.core
        swartz.models)
  (:gen-class))

(defroutes app-routes
  (GET "/" req (ctrl/get-homepage req))

  (GET "/login" req (ctrl/get-login req))
  (GET "/signup" req (ctrl/get-signup req))
  (POST "/signup" req (ctrl/post-signup req))
  (GET "/logout" req (friend/logout* (redirect "/")))

  (resources "/")
  (not-found "Page not found."))

(defn- get-users-map []
  (into {} (map (fn [u]
                  [(:username u) u])
                (select users))))

(def handler
  (-> app-routes
      (friend/authenticate {:credential-fn (fn [user]
                                             (creds/bcrypt-credential-fn
                                              (get-users-map)
                                              user))
                            :workflows [(workflows/interactive-form)]})
      (wrap-defaults site-defaults)
      (site)))

(defn -main
  "Start a ring/jetty server."
  [& args]
  (run-jetty handler {:port 3000}))
