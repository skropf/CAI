(ns cai.speech-api
  (:gen-class)
  (:require [clojure.string :as str]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [environ.core :refer [env]]
            [cai.ffmpeg :as ffmpeg]
            [clojure.java.io :as io]
            [byte-streams :as bs]))
(import (org.apache.commons.codec.binary Base64))

(defn audio-to-b64 [audio-byte-array]
  (String. (Base64/encodeBase64 audio-byte-array)))

(defn request-body [audio-base64]
  {"config" {"encoding" "LINEAR16"
             "sampleRateHertz" 32000
             "languageCode" "en-US"}
   "audio" {"content" audio-base64}})

(defn call-speech-api [audio-base64]
  @(http/post "https://speech.googleapis.com/v1/speech:recognize"
     { :query-params {"key" (env :google-api-key)}
       :headers {"Content-Type" "application/json"}
       :body (json/write-str (request-body audio-base64))
       :insecure? true}))


(defn handle-speech-response [{:keys [status headers body error]}]
  (if (= status 200)
      (let [result (first (get-in (json/read-str body :key-fn keyword) [:results]))]
        (get-in (first (get-in result [:alternatives])) [:transcript]))))

(defn analyze [url]
  (let [output (first (str/split (last (str/split url #"/")) #"\."))
        filepath (str "audio-files/" output ".wav")]
    (ffmpeg/ffmpeg! :i url :loglevel "panic" :acodec "pcm_s16le" :ac 1 :ar 32000 filepath)
    (-> (bs/to-byte-array (java.io.File. filepath))
        audio-to-b64
        call-speech-api
        handle-speech-response)))
