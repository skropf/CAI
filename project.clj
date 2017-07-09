(defproject cai "0.1.0-SNAPSHOT"
  :description "Facebook Messenger Bot in Clojure"
  :url "https://github.com/skropf/CAI"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.1"]
                 [org.clojure/core.async "0.3.443"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [clojure-future-spec "1.9.0-alpha17"]
                 [compojure "1.5.1"]
                 [http-kit "2.2.0"]
                 [ring/ring-defaults "0.2.1"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [net.data-rx/google-cloud "0.2.0" :exclusions [cheshire commons-codec]]
                 [environ "1.1.0"]
                 [fb-messenger "0.4.0" :exclusions [cheshire clj-http]]]
  :target-path #=(eval (if (= "vagrant" (System/getenv "USER")) "/tmp/target/%s" "target"))
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.1.0"]]
  ;:hooks [environ.leiningen.hooks]
  :ring {:handler cai.core/app
         :open-browser? false}
  :aot [cai.core]
  :uberjar-name "cai-standalone.jar")
  ; :profiles {:default [:base :dev :user]
  ;            #_:production #_{:env {:production false}}})
