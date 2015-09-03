(ns dsp.util)


(defn sample-times
  "Returns the sample times for a sequence of samples, given a sample
   rate sr."
  [samples sr]
  (map #(/ %1 (double sr)) (range (count samples))))


(defn rms
  "Returns the RMS for a sequence, takes an optional :window-size."
  [xn & {:keys [window-size] :or {window-size (count xn)}}]
  (let [squares (map #(* %1 %1) xn)
        sum     (reduce + squares)]
    (java.lang.Math/sqrt (/ sum window-size))))


(defn rms-at-index
  "Returns the RMS for a value at a given index. Uses the values
   before and after the current index to emulate an RMS window of the
   given size."
  [i x xn window-size]
  (let [n       (count xn)
        xn-vec  (vec xn)
        from    (max 0 (- i (/ window-size 2)))
        to      (min n (+ i (/ window-size 2)))
        values  (subvec xn-vec from to)]
    (rms values :window-size window-size)))


(defn rms-envelope
  "Returns the RMS envelope for a signal, given a specific RMS window
   size."
  [samples window-size]
  (map-indexed #(rms-at-index %1 %2 samples window-size) samples))
