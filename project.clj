(defproject dsp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://gezeiten.org"
  :license {:name "GNU General Public License version 2"
            :url "http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [incanter/incanter-core "1.9.0"]
                 [incanter/incanter-charts "1.9.0"]]
  :main ^:skip-aot dsp.core
  :target-path "target/%s"
  :plugins []
  :profiles {:uberjar {:aot :all}})
