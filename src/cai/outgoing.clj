(ns cai.outgoing
  (:gen-class)
  (:require [fb-messenger.templates :as templates]
            [cai.speech-api :as speech-api]
            [cai.cleverbot :as cleverbot]))

(def user-map (atom {}))

(defn echo [message-text]
  [{:message (templates/text-message message-text)}])

(defn error []
  [{:message (templates/text-message "Sorry, I didn't get that! :(")}])

(defn reply-to-image [url]
  [{:message (templates/image-message url)}])


(defn reply [sender-id type input]
  (cond
    (= type "text") (def text (str input))
    (= type "image") (reply-to-image input)
    (= type "audio") (def text (speech-api/stt input))
    (= type "video") (def text (speech-api/stt input)))

  (let [cs-old (get-in @user-map [(keyword sender-id) :cs])
        {:strs [cs clever_output]} (cleverbot/get-cleverbot-answer text cs-old)]
    (swap! user-map (fn [x] (update-in x [(keyword sender-id) :cs] (fn [y] cs)))) ;update cs from sender-id
    (println (str "sent: " text
                  "\nreceived: " clever_output))
    [{:action "typing_on"}
     {:message (templates/text-message clever_output)}]))

;TODO
;user-map + timestamp - to delete user after ~24h non usage
;image: returns nasa pic of the day random over last year
