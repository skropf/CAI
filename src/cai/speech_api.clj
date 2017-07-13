(ns cai.speech-api
  (:gen-class)
  (:require [clojure.string :as s]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]]))
(import (org.apache.commons.codec.binary Base64))


(defn download-audio [url]
  (let [response (http/get url {:as :byte-array})]
    (:body @response)))

(defn audio-to-b64 [audio]
  (String. (Base64/encodeBase64 audio)))

(defn request-body [b64audio]
  {"config" {"encoding" "AMR"
             "sampleRateHertz" 8000
             "languageCode" "en-US"}
   "audio" {"content" b64audio}})

(defn call-speech-api [b64audio]
  (println b64audio)
  @(http/post "https://speech.googleapis.com/v1/speech:recognize"
     { :query-params {"key" (env :google-api-key)}
       :headers {"Content-Type" "application/json"}
       :body (json/write-str (request-body b64audio))
       :insecure? true}))


(defn handle-speech-response [{:keys [status headers body error]}]
  (if (= status 200)
      (println (str status "-" headers "-" body "-" error))
        ;(json/read-str body :key-fn keyword
        ;  :responses
        ;  first)
      (println body)))


(defn analyze [url]
  (-> (download-audio url)
      audio-to-b64
      call-speech-api
      handle-speech-response))

;;IMPORTANT: code works so far, but response empty. need to convert audio from
;;mp4(aac) to valid format!!!
;;(see https://cloud.google.com/speech/reference/rest/v1/RecognitionConfig#AudioEncoding)
