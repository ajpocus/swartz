(ns swartz.models.core
  (:require [environ.core :refer [env]]))

(def db {:connection-uri (env :database-url)})
