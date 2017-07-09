(ns facebook-example.api.vision
  (:gen-class)
  (:require [clojure.string :as s]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]]))
(import (org.apache.commons.codec.binary Base64))

(def SPEECH-KEY (env :google-application-credentials))


(defn download-audio [url]
  (let [response (http/get url {:as :byte-array})]
    (:body @response)))

(defn audio-to-b64 [audio]
  (String. (Base64/encodeBase64 audio)))


(defn call-speech-api [b64-audio]
    (as-> b64-audio data
          @(http/post "https://speech.googleapis.com/v1/speech:recognize"
                     {  :query-params {"key" SPEECH-KEY}
                        :headers {"Content-Type" "application/json"}
                        :body (json/write-str (request-body data))
                        :insecure? true})))


(defn handle-speech-response [{:keys [status headers body error]}]
  (if (= status 200)
      (-> (json/read-str body :key-fn keyword)
          :responses
          first)
      (do (println "ERROR: Call to Vision API failed. Maybe check your vision-key in profiles.clj?")
          (println body))))


(defn analyze [url]
  (-> (download-audio url)
      audio-to-b64
      call-speech-api
      handle-speech-response))
