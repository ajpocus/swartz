(defproject swartz "1.0.0"
  :description "A simple forum-ish web app."
  :url "https://github.com/ajpocus/swartz"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.4"]
                 [compojure "1.3.2"]
                 [enlive "1.1.5"]
                 [yesql "0.4.0"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ragtime "0.3.8"]
                 [com.cemerick/friend "0.2.1"]
                 [clj-time "0.9.0"]]
  :plugins [[lein-ring "0.9.2"]
            [ragtime/ragtime.lein "0.3.8"]]

  :ring {:handler swartz.core/handler
         :auto-reload? true
         :auto-refresh true}
  :main ^:skip-aot swartz.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:ragtime {:migrations ragtime.sql.files/migrations
                             :database "jdbc:postgresql:swartz"}}
             :test {:ragtime {:migrations ragtime.sql.files/migrations
                              :database "jdbc:postgresql:swartz_test"}}})
