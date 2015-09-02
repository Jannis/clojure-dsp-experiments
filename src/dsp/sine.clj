(ns dsp.sine)


(defn sample-points
  "Returns a lazy sequence with as many elements as sample points are
   are required to represent t seconds at a sample rate of sr."
  [t sr]
  (range (* t sr)))


(defn- sine-sample
  "Returns the sine wave sample at time t for a sine wave with the
   frequency f given sample rate sr."
  [t f sr]
  (java.lang.Math/sin (* t f 2 (/ java.lang.Math/PI sr))))


(defn sine
  "Returns the samples for t seconds of a sine wave with the frequency
   f given a sample rate sr."
  [t f sr]
  (->> (sample-points t sr)
       (map #(sine-sample %1 f sr))))
