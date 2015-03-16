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
            [swartz.controllers :as ctrl]
            [swartz.middleware :as ware])
  (:use korma.core
        swartz.models)
  (:gen-class))

(defroutes app-routes
  (GET "/" req (ctrl/get-posts req))

  (GET "/login" req (ctrl/get-login req))
  (GET "/signup" req (ctrl/get-signup req))
  (POST "/signup" req (ctrl/post-signup req))
  (GET "/logout" [] (friend/logout* (redirect "/")))

  (GET "/posts" req (ctrl/get-posts req))
  (GET "/posts/new" req (friend/authenticated (ctrl/new-post req)))
  (POST "/posts" req (friend/authenticated (ctrl/create-post req)))
  (GET "/posts/:id" req (ctrl/get-post req))

  (POST "/posts/:post_id/notes" req
        (friend/authenticated (ctrl/create-note req)))
  (GET "/posts/:post_id/notes/:note_id" req (ctrl/get-note req))

  (resources "/")
  (not-found "Page not found."))

(defn- get-user-map []
  (into {} (map (fn [u]
                  [(:username u) u])
                (select user))))

(def handler
  (-> app-routes
      (friend/authenticate {:credential-fn (fn [user]
                                             (creds/bcrypt-credential-fn
                                              (get-user-map)
                                              user))
                            :workflows [(workflows/interactive-form)]})
      (wrap-defaults site-defaults)
      (site)))

(defn -main
  "Start a ring/jetty server."
  [& args]
  (run-jetty handler {:port 3000}))
