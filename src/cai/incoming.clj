(ns cai.incoming
  (:gen-class)
  (:require [cai.outgoing :as outgoing]
            [fb-messenger.templates :as templates]))

; Uncomment if you want to set a persistent menu in your bot:
; (facebook/set-messenger-profile
;      {:get_started {:payload "GET_STARTED"}
;       :persistent_menu [{:locale "default"
;                          :call_to_actions [{:title "Help"
;                                             :type "postback"
;                                             :payload "GET_HELP"}]}]})
;                                             {:title "Show me some bots"}]))
;                                             :type "postback"
;                                             :payload "GET_LEMMINGS_BOTS"}

(defn on-message [event]
  ; Called by handle-message when the user has sent a text message
  (println "on-message event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        message (get-in event [:message :text])]
    (outgoing/reply-to-text sender-id message)))


(defn on-quick-reply [event]
  ; Called by handle-message when the user has tapped a quick reply
  ; https://developers.facebook.com/docs/messenger-platform/send-api-reference/quick-replies
  (println "on-quickreply event:")
  (println event))


(defn on-postback [event]
  ; Called by handle-message when the user has tapped a postback button
  ; https://developers.facebook.com/docs/messenger-platform/send-api-reference/postback-button
  (println "on-postback event:")
  (println event))


(defn on-attachments [event]
  ; Called by handle-message when the user has sent a file or sticker
  (println "on-attachment event:")
  (println event)
  (let [sender-id (get-in event [:sender :id])
        recipient-id (get-in event [:recipient :id])
        time-of-message (get-in event [:timestamp])
        attachments (get-in event [:message :attachments])
        attachment (first attachments)
        type (get-in attachment [:type])
        url (get-in attachment [:payload :url])]
    (cond
      (= type "image") (outgoing/reply-to-image sender-id url)
      (= type "audio") (outgoing/reply-to-audio-or-video sender-id url)
      (= type "video") (outgoing/reply-to-audio-or-video sender-id url))))
