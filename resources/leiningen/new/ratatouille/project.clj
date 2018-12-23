(defproject {{project.name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:skip-aot {{core.namespace}}
  :source-paths ["src/clj" "src/cljs"]
  :resource-paths ["resources" "target"]
  :target-path "target/%s"
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :profiles {:dev
             {:dependencies [[org.clojure/clojurescript "1.10.439"]
                             [com.bhauman/figwheel-main "0.2.0"]]}
             :uberjar
             {:aot :all}})
