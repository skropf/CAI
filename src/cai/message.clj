(ns cai.message
  (:gen-class)
  (:require [fb-messenger.templates :as templates]))

(defn send [message-text]
  (templates/text-message message-text))
