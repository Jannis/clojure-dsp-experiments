(ns dsp.util)


(defn sample-times
  "Returns the sample times for a sequence of samples, given a sample
   rate sr."
  [samples sr]
  (map #(/ %1 (double sr)) (range (count samples))))
