(ns swartz.models.core
  (:require [environ.core :refer [env]]
            [clojure.string :as str]))

(def db {:connection-uri (str/replace (env :database-url)
                                      #":postgres:"
                                      ":postgresql:")
         :classname "org.postgresql.Driver"})
