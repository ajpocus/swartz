(defproject swartz "0.1.0-SNAPSHOT"
  :description "A Reddit clone in Clojure (named for Aaron Swartz)"
  :url "http://example.com/FIXME"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.4"]
                 [compojure "1.3.2"]
                 [enlive "1.1.5"]
                 [korma "0.4.0"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [lobos "1.0.0-beta3"]
                 [com.cemerick/friend "0.2.1"]
                 [clj-time "0.9.0"]]
  :plugins [[lein-ring "0.9.2"]]
  :ring {:handler swartz.core/handler
         :auto-reload? true
         :auto-refresh true}
  :main ^:skip-aot swartz.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
