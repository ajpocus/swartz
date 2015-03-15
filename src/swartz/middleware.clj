(ns swartz.middleware
  (:require [clojure.tools.logging :as log]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [cemerick.friend :as friend]))

(defn log-request [handler]
  "Logs requests in the following form: request-method \"uri\" status-code"
  (fn [req]
    (let [res (handler req)
          meth (->> (:request-method req)
                    str
                    rest
                    (apply str)
                    str/upper-case)]
      (log/info (str/join #" " [meth (str \" (:uri req) \") (:status res)]))
      res)))
