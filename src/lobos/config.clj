(ns lobos.config
  (:use lobos.connectivity))

(def db
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :user "austin"
   :password "notasecurepass"
   :subname "//localhost:5432/swartz"})

(open-global db)
