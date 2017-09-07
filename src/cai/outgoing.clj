(ns cai.outgoing
  (:gen-class)
  (:require [fb-messenger.templates :as templates]
            [clojure.string :as str]
            [cai.speech-api :as speech-api]
            [cai.cleverbot :as cleverbot]
            [clj-time.core :as time]
            [clj-time.format :as timef]
            [clojure.data.json :as json]
            [clj-http.client :as client]))


(def user-map (atom {}))

(defn echo [message-text]
  [{:message (templates/text-message message-text)}])

(defn error []
  [{:message (templates/text-message "Sorry, I didn't get that! :(")}])

(defn reply-to-text [sender-id text]
  (let [cs-old (get-in @user-map [(keyword sender-id) :cs])
        {:strs [cs clever_output]} (cleverbot/get-cleverbot-answer text cs-old)]
    (swap! user-map (fn [x] (update-in x [(keyword sender-id) :cs] (fn [y] cs)))) ;update cs from sender-id
    [{:action "typing_on"}
     {:message (templates/text-message clever_output)}]))


(defn reply-to-image [sender-id image-url]
  (let [days (mod (reduce + (map #(int %) (seq image-url))) (* 3 365))
        date (time/minus (time/now) (time/days days))
        parseddate (timef/unparse (timef/formatter "YYYY-MM-dd") date)
        {:strs [title explanation hdurl url]} (json/read-str (get-in (client/get (str "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&date=" parseddate)) [:body]))
        sentences (str/split explanation #"\.")]
    [{:action "typing_on"}
     {:message (templates/text-message (str "APOD @" parseddate))}
     {:action "typing_on"}
     {:message (templates/text-message title)}
     {:action "typing_on"}
     (if (str/includes? (str url) ".jpg")
       {:message (templates/image-message url)}
       {:message (templates/text-message url)})]))

  
(defn reply-to-audio-or-video [sender-id url]
  (let [text (speech-api/stt url)
        cs-old (get-in @user-map [(keyword sender-id) :cs])
        {:strs [cs clever_output]} (cleverbot/get-cleverbot-answer text cs-old)]
    (swap! user-map (fn [x] (update-in x [(keyword sender-id) :cs] (fn [y] cs)))) ;update cs from sender-id
    [{:action "typing_on"}
     {:message (templates/text-message clever_output)}]))
