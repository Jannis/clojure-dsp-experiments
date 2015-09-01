(ns dsp.comp
  (:require [dsp.amp :refer :all]))



(defn limit-sample
  "Limits a sample by a given dB value."
  [x db]
  (let [limit (db2amp db)]
    (if (< x 0)
      (max x (* limit -1))
      (min x limit))))


(defn stupid-limiter
  "A very harsh and stupid limiter."
  [samples db]
  (map #(limit-sample %1 db) samples))
