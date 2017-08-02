(ns cai.cleverbot
  (:require [clojure.string :as str]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [environ.core :refer [env]]))


(def cleverbot-credentials (env :credentials-chatbot))

(defn get-cleverbot-answer [question cs]
  (json/read-str (str/replace (get-in (client/get (str "http://www.cleverbot.com/getreply?key=" cleverbot-credentials "&input=" question "&cs=" cs)) [:body]) ":" " ")))
