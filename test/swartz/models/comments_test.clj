(ns swartz.models.comments-test
  (:require (swartz.models [users :as users]
                           [posts :as posts]
                           [comments :as comments]))
  (:use clojure.test
        swartz.test-commons))

(use-fixtures :each clean-db)
