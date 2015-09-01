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
  (doseq [samples (comp-test-sines 100 5512)]
    (let [times (sample-times samples 5512)
          limited (stupid-limiter samples -6.0)]
      (incanter/view (-> (charts/xy-plot times samples)
                         (charts/add-lines times limited))))))
