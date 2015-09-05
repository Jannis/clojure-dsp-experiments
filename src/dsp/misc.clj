(ns dsp.misc
  (:require [incanter.core :refer :all]
            [incanter.charts :refer :all]
            [dsp.sine :refer :all]
            [dsp.util :refer :all]
            [dsp.amp :refer :all]))


(defn vca
  [in cv]
  (* in (java.lang.Math/exp cv)))


(defn slope
  [ratio]
  (- (/ ratio) 1))


(defn half-wave-rectifier
  [x]
  (max x 0))


(defn cv
  [in threshold ratio]
  (* (slope ratio)
     (half-wave-rectifier (- (java.lang.Math/log in)
                             (java.lang.Math/log threshold)))))


(defn compress1
  [ins threshold ratio]
  (let [ins-abs  (map #(java.lang.Math/abs    %1) ins)
        ins-sign (map #(java.lang.Math/signum %1) ins)
        comp-abs (map #(vca %1 (cv %1 threshold ratio)) ins-abs)
        comp     (map #(* %1 %2) ins-sign comp-abs)]
    comp))


(defn half-wave-rectifier2
  [x]
  (max 0 x))


(defn soft-knee-rectifier
  [width x]
  (let [log-width (java.lang.Math/log width)]
    (cond
      (< x (- (/ log-width 2)))
        0
      (and (>= x (- (/ log-width 2)))
           (<  x (+ (/ log-width 2))))
        (* (/ (* 2 log-width))
           (* (+ x (/ log-width 2))
              (+ x (/ log-width 2))))
      :else
        x)))

(defn cv2
  [in rectifier threshold ratio]
  (* (slope ratio) (rectifier (- (java.lang.Math/log in)
                                 (java.lang.Math/log threshold)))))


(defn rms-detector
  [xn tau sr]
  (reduce (fn [out x]
            (let [alpha (- 1 (java.lang.Math/exp (- (/ (* sr tau)))))
                  prev (last out)
                  next (java.lang.Math/sqrt (+ (* alpha x x)
                                               (* (- 1 alpha) prev prev)))]
              (conj out next)))
          [(first xn)] (rest xn)))


(defn compress2
  [ins & {:keys [threshold ratio knee makeup]
          :or   {threshold 0.0
                 ratio     1.0
                 knee      0.0
                 makeup    1.0}}]
  (let [sign      (map #(java.lang.Math/signum %1) ins)
        abs       (map #(java.lang.Math/abs %1) ins)
        rectifier (if (>= knee 0)
                    (partial soft-knee-rectifier knee)
                    (partial half-wave-rectifier2))
        comp      (map #(vca %1 (cv2 %1 rectifier threshold ratio)) abs)
        out-gain  (map #(* %1 makeup) comp)
        out-sign  (map * sign out-gain)]
    out-sign))


(defn -main
  [& args]
  (let [ins         (range 0.0 1.0 0.001)
        outs-6db-1  (compress2 ins :threshold (db2amp  -6)
                                   :ratio     1.0
                                   :knee      (java.lang.Math/exp 0.5))
        outs-6db-2  (compress2 ins :threshold (db2amp  -6)
                                   :ratio     2.0
                                   :knee      (java.lang.Math/exp 0.5))
        outs-6db-2g (compress2 ins :threshold (db2amp  -6)
                                   :ratio     2.0
                                   :knee      (java.lang.Math/exp 0.5)
                                   :makeup    1.5)
        outs-6db-4  (compress2 ins :threshold (db2amp  -6)
                                   :ratio     4.0
                                   :knee      (java.lang.Math/exp 0.5))
        outs-6db-20 (compress2 ins :threshold (db2amp  -6)
                                   :ratio     20.0
                                   :knee      (java.lang.Math/exp 0.5))
        outs-12db-2 (compress2 ins :threshold (db2amp -12)
                                   :ratio     2.0
                                   :knee      (java.lang.Math/exp 0.5))
        sine-out        (sine 1.0 10 1000)
        sine-times      (sample-times sine-out 1000)
        sine-rms        (rms-detector sine-out (* 2.2 0.1) 1000)
        sine-6db-2      (compress2 sine-out :threshold (db2amp -6)
                                            :ratio     2.0)
        sine-6db-2-knee (compress2 sine-out :threshold (db2amp -6)
                                            :ratio     2.0
                                            :knee      (java.lang.Math/exp 0.5))
        sine-6db-2-rms  (compress2 sine-out :threshold (db2amp -6)
                                            :ratio     2.0)]
    (do
      (view (-> (xy-plot   ins outs-6db-1)
                (add-lines ins outs-6db-2)
                (add-lines ins outs-6db-2g)
                (add-lines ins outs-6db-4)
                (add-lines ins outs-6db-20)
                (add-lines ins outs-12db-2)))
      (view (-> (xy-plot   sine-times sine-out
                           :legend true
                           :series-label "Sine @ 0dB")
                (add-lines sine-times sine-rms
                           :series-label "RMS")
                (add-lines sine-times sine-6db-2)
                (add-lines sine-times sine-6db-2-knee))))))
