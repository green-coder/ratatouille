(ns ratatouille.node.clojurescript
  (:require [leiningen.new.templates :refer [->files
                                             name-to-path
                                             multi-segment
                                             project-name
                                             sanitize-ns
                                             slurp-resource]]))

(def node
  {:keyword :clojurescript
   :name "+cljs"
   :description "Uses Clojurescript via Figwheel Main."
   :dependencies []
   :context {:project {:source-paths ["src/cljs"]
                       :dependencies [:clojure :clojurescript]
                       :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
                                 "fig:dev"   ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
                                 "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]}
                                 ;"fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" hello-world.test-runner]}
                       :profiles {:dev {:dependencies [:figwheel-main :rebel-readline-cljs]}}}
             :main {:cljs
                    ^{:ctx [:project :ns :name]}
                     (fn [project-ns]
                       (let [namespace (str project-ns ".core")]
                         {:path (str "cljs/" (name-to-path namespace) ".cljs")
                          :ns {:name namespace
                               :meta {:figwheel-hooks true}}}))}}})
