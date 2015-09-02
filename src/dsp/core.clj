(ns dsp.core
  (:gen-class)
  (:require [dsp.comp :refer :all]
            [dsp.sine :refer :all]
            [dsp.test :refer :all]
            [dsp.util :refer :all]
            [incanter.core :as incanter]
            [incanter.charts :as charts]))


(defn -main
  [& args]
  (doseq [samples (comp-test-signals 10 1000)]
    (let [times      (sample-times samples 1000)
          ; limited    (stupid-limiter samples -6.0)
          compressed (stupid-compressor samples -6.0 2.0)]
      (incanter/view (-> (charts/xy-plot times samples)
                        ;  (charts/add-lines times limited)
                         (charts/add-lines times compressed))
                     :width 1200 :height 600))))
