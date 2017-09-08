;CODE from https://github.com/runexec/ffmpeg-clj/blob/master/src/ffmpeg_clj/core.clj
(ns cai.ffmpeg
  (:gen-class)
  (:require
   [clojure.java.shell :refer [sh]]
   [clojure.string :as s]))

(def ^:dynamic *bin* "ffmpeg")

(defn cmd [& argv]
  (->> argv
       (map #(if (keyword? %) (str "-" (name %)) (str %)))
       (into [*bin*])))

(defn bin [] *bin*)

(defn ffmpeg! [& args]
  (let [cmd! (apply sh (apply cmd args))
        {:keys [exit out err]} cmd!]
    (when-not (zero? exit)
      (throw
       (Exception. err)))
    out))

(defn version []
  (as-> (ffmpeg! "-version") o
        (re-find #"version \S+" o)
        (s/split o #" ")
   (last o)))
