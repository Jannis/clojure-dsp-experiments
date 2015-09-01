(ns dsp.core
  (:gen-class)
  (:require [dsp.sine :refer :all]
            [dsp.test :refer :all]
            [dsp.util :refer :all]
            [incanter.core :as incanter]
            [incanter.charts :as charts]))


(defn -main
  [& args]
  (doseq [samples (comp-test-sines 2 10)]
    (let [times (sample-times samples 10)]
        (incanter/view (charts/xy-plot times samples)))))
