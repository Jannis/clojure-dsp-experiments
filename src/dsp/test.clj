(ns dsp.test
  (:require [dsp.amp :refer :all]
            [dsp.sine :refer :all]))


(defn simple-sine
  "Returns a sine wave with amplitude db and frequency f, lasting for
   t seconds given a sample rate sr."
  [db t f sr]
  (amplifydb db (sine t f sr)))


(defn jumping-sine
  "Returns a signal with dB jumps, based on a sine wave. An example
   would be (jumping-sine [[1.0 -3.0] [2.0 0.0]] 440 44100), which
   returns 1 second of a 440Hz sine wave at -3dB, followed by 2
   seconds of a 440Hz sine wave at 0dB, all given a sample rate of
   44100Hz."
  [segments f sr]
  (flatten
    (for [segment segments]
      (let [[t db] segment]
        (simple-sine db t f sr)))))


(defn simple-rect
  [amplitude t sr]
  (repeat (count (sample-points t sr)) amplitude))


(defn jumping-rect
  [segments sr]
  (flatten
    (for [segment segments]
      (let [[t amplitude] segment]
        (simple-rect amplitude t sr)))))


(defn comp-test-signals
  "Returns a list of signals for testing compressor filters."
  [f sr]
  (list
    ; 3s of an unnatural rectangular signal
    ; (jumping-rect [[1.0 0.0] [1.0 1.0] [1.0 -1.0] [2.0 0.0]] sr)
    ; 1s of a constantly increasing signal
    (map #(* (/ 1.0 sr) %1) (range sr))))
    ; ; 3s sine wave at 0dB
    ; (simple-sine 0 3.0 f sr)
    ; ; 3s sine wave at -6dB
    ; (simple-sine -6.0 3.0 f sr)
    ; ; 3s sine wave at -12dB
    ; (simple-sine -12.0 3.0 f sr)
    ; ; 1s sine wave at -18dB, 1s at -12dB, 1s at -18dB
    ; (jumping-sine [[1.0 -18.0] [1.0 -12.0] [1.0 -18.0]] f sr)
    ; ; 1s sine wave at -12dB, 1s at -6dB, 1s at -12dB
    ; (jumping-sine [[1.0 -12.0] [1.0  -6.0] [1.0 -12.0]] f sr)
    ; ; 1s at -6dB, 1s at -3dB, 1s at -6dB
    ; (jumping-sine [[1.0  -6.0] [1.0  -3.0] [1.0  -6.0]] f sr)
    ; ; 1s at -18dB, 1s at -6dB, 1s at -18dB
    ; (jumping-sine [[1.0 -18.0] [1.0  -6.0] [1.0 -18.0]] f sr)
    ; ; 1s at -12dB, 1s at -3dB, 1s at -12dB
    ; (jumping-sine [[1.0 -12.0] [1.0  -3.0] [1.0 -12.0]] f sr)
    ; ; 1s at -6dB, 1s at 0dB, 1s at -6dB
    ; (jumping-sine [[1.0  -6.0] [1.0   0.0] [1.0  -6.0]] f sr)))
