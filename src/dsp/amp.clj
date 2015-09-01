(ns dsp.amp)


(defn db2amp
  "Converts a dB value to an amplification factor (e.g. -6dB to 0.5)."
  [db]
  (java.lang.Math/pow 10 (/ db 20)))


(defn amplifydb
  "Amplifies a signal by a given dB value."
  [db samples]
  (map #(* (db2amp db) %1) samples))
