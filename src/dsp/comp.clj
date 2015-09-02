(ns dsp.comp
  (:require [dsp.amp :refer :all]))



(defn- limit-sample
  "Limits a sample by a given dB value."
  [x threshold]
  (let [threshold (db2amp threshold)]
    (if (< x 0)
      (max x (* threshold -1))
      (min x threshold))))


(defn stupid-limiter
  "A very harsh and stupid limiter."
  [samples threshold]
  (map #(limit-sample %1 threshold) samples))


(defn apply-ratio
  [x threshold ratio]
  (let [threshold (db2amp threshold)
        sign      (java.lang.Math/signum x)
        over      (- (java.lang.Math/abs x) threshold)]
    (if (> over 0)
      (* sign (+ threshold (* over (/ 1.0 ratio))))
      x)))


(defn stupid-level
  [amplitudes]
  (map #(java.lang.Math/abs %1) amplitudes))


(defn stupid-overshoot
  [levels threshold]
  (map #(max (- %1 (db2amp threshold)) 0) levels))


(defn stupid-gain-reduction
  [overshoot ratio]
  (map #(/ %1 ratio) overshoot))


(defn apply-sample-gain-reduction
  [x o r]
  (* (java.lang.Math/signum x)
     (+ (- (java.lang.Math/abs x) o) r)))


(defn apply-gain-reduction
  [amplitudes overshoot reduction]
  (map apply-sample-gain-reduction amplitudes overshoot reduction))
  ; (map #(- %1 (* (java.lang.Math/signum %1) %2))
      ;  amplitudes reduction))


(defn stupid-compressor
  "A stupid compressor with only threshold and ratio parameters."
  [samples threshold ratio]
  (let [level          (stupid-level samples)
        overshoot      (stupid-overshoot level threshold)
        gain-reduction (stupid-gain-reduction overshoot ratio)]
    (apply-gain-reduction samples overshoot gain-reduction)))
