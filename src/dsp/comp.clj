(ns dsp.comp
  (:require [dsp.amp :refer :all]
            [incanter.core :refer :all]))



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


(defn apply-makeup-gain
  [amplitudes gain]
  (amplifydb gain amplitudes))


(defn stupid-compressor
  "A stupid compressor with only threshold and ratio parameters."
  [samples & {:keys [threshold ratio makeup-gain]
              :or   {threshold   0.0
                     ratio       1.0
                     makeup-gain 0.0}}]
  (let [level          (stupid-level samples)
        overshoot      (stupid-overshoot level threshold)
        gain-reduction (stupid-gain-reduction overshoot ratio)
        reduced        (apply-gain-reduction samples overshoot
                                             gain-reduction)]
    (apply-makeup-gain reduced makeup-gain)))


(defn soft-knee
  [x x0 x1 p0 p1 m0 m1]
  (let [width (- x1 x0)
        t     (/ (- x x0) width)
        t2    (* t t)
        t3    (* t t t)
        _m0   (* m0 width)
        _m1   (* m1 width)
        ct0   p0
        ct1   _m0
        ct2   ($= -3 * p0 - 2 * _m0 + 3 * p1 - _m1)
        ct3   ($= 2 * p0 + _m0 - 2 * p1 + _m1)]
    ($= ct3 * t3 + ct2 * t2 + ct1 * t + ct0)))


(defn compress
  [x threshold ratio]
  (+ threshold (/ (- x threshold) ratio)))


(defn compress-with-knee
  [x threshold ratio knee]
  (let [knee-min  (- threshold (/ knee 2.0))
        knee-max  (+ threshold (/ knee 2.0))
        knee-stop (compress knee-max threshold ratio)]
    (cond (<= x knee-min) x
          (>  x knee-max) (compress x threshold ratio)
          :else           (soft-knee x knee-min knee-max
                                       knee-min knee-stop
                                       1.0 (/ ratio)))))


(defn soft-knee-compressor-curve
  [& {:keys [threshold ratio knee]
      :or   {threshold 0.0
             ratio     1.0
             knee      0.0}}]
  (let [r   (max 1.0 ratio)
        k   (max 0.0 knee)
        t   (min 0.0 threshold)
        in  (map #(- (* %1 (/ 64 1000)) 64.0) (range 1000))
        out (map #(compress-with-knee %1 t r k) in)]
    (list in out)))


(defn soft-knee-compressor
  "A slightly more advanced, yet still stupid compressor with
   soft-knee support."
  [samples & {:keys [threshold ratio makeup-gain knee]
              :or   {threshold    0.0
                     ratio        1.0
                     makeup-gain  0.0
                     knee         0.0}}]
  (let [r (max 1.0 ratio)
        k (max 0.0 knee)
        t (min 0.0 threshold)]
    (for [x samples]
      (let [sign  (java.lang.Math/signum x)
            level (java.lang.Math/abs x)]
        (* sign (db2amp (compress-with-knee (amp2db level) t r k)))))))
