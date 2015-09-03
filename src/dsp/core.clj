(ns dsp.core
  (:gen-class)
  (:require [dsp.comp :refer :all]
            [dsp.sine :refer :all]
            [dsp.test :refer :all]
            [dsp.util :refer :all]
            [dsp.amp :refer :all]
            [incanter.core :as incanter]
            [incanter.charts :as charts]))


(defn -main
  [& args]
  (do
    (let [[in1 out1] (soft-knee-compressor-curve :threshold -24.0
                                                 :ratio       6.0
                                                 :knee        0.0)
          [in2 out2] (soft-knee-compressor-curve :threshold -24.0
                                                 :ratio       6.0
                                                 :knee       24.0)]
      (incanter/view (-> (charts/xy-plot in1 out1
                                         :title "Soft Knee Compressor Curve"
                                         :x-label "Input"
                                         :y-label "Output"
                                         :legend true
                                         :series-label "Ratio 6:1, Knee 0dB")
                         (charts/add-lines in2 out2
                                           :series-label "Ratio 6:1, Knee 24dB")
                         (charts/add-lines [-24 -24] [-64 0]
                                           :series-label "Threshold -24dB"))
                     :width 600 :height 600))
    (doseq [samples (comp-test-signals 10 1000)]
      (let [times (sample-times samples 1000)
            compressed1 (soft-knee-compressor samples
                                              :threshold -6.0
                                              :ratio      6.0
                                              :knee       0.0)]
        (incanter/view (-> (charts/xy-plot times samples
                                           :title "Compressed Test Signal (Threshold: -6dB)"
                                           :x-label "Time"
                                           :y-label "Amplitude"
                                           :legend true
                                           :series-label "Original Signal")
                            (charts/add-lines times compressed1
                                              :series-label "Ratio 6:1, Knee 0dB"))
                       :width 1000 :height 600)))))
    ; (doseq [samples (comp-test-signals 10 1000)]
    ;   (let [times       (sample-times samples 1000)
    ;         ; limited    (stupid-limiter samples -6.0)
    ;         compressed1 (stupid-compressor samples
    ;                                        :threshold -6.0
    ;                                        :ratio 10.0
    ;                                        :makeup-gain 0.0)
    ;         compressed2 (soft-knee-compressor samples
    ;                                           :threshold -6.0
    ;                                           :ratio 10.0
    ;                                           :makeup-gain 0.0
    ;                                           :knee 6.0)
    ;         ]
    ;     ; (incanter/view (-> (charts/xy-plot in1 out1)
    ;     ;                    (charts/add-lines in2 out2)
    ;     ;                   ;  (charts/add-function identity -64 0 :step-size 0.064)
    ;     ;                    (charts/add-lines [-24 -24] [-64 0]))
    ;     ;                :width 600 :height 600))))
    ;     (incanter/view (-> (charts/xy-plot times samples))
    ;                        (charts/add-lines times compressed1)
    ;                        (charts/add-lines times compressed2)
    ;                    :width 1200 :height 600)
    ;                       ;  (charts/add-lines times limited)
