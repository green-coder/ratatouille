(ns ratatouille.node.latest-artifact)

;; All the artifacts that Ratatouille is going to use are grouped here, making it easier to check for obsolescence.
;; This node is treated specially, it is inserted in front of everything else in the graph process.
(def node
  {:keyword :latest-artifacts
   :name "+latest-artifacts"
   :description "Provides the latest artifacts in the project's context."
   :context {:latest-artifacts '{:clojure [org.clojure/clojure "1.10.0"]
                                 :clojurescript [org.clojure/clojurescript "1.10.439"]
                                 :figwheel-main [com.bhauman/figwheel-main "0.1.9"]
                                 :rebel-readline-cljs [com.bhauman/rebel-readline-cljs "0.1.4"]
                                 :ancient [lein-ancient "0.6.15"]
                                 :integrant [integrant "0.7.0"]
                                 :integrant-repl [integrant/repl "0.3.1"]
                                 :ring-core [ring/ring-core "1.7.1"]
                                 :ring-json [ring/ring-json "0.4.0"]
                                 :ring-defaults [ring/ring-defaults "0.3.2"]
                                 :http-kit [http-kit "2.3.0"]
                                 :reitit-core [metosin/reitit-core "0.2.10"]
                                 :rum [rum "0.11.3"]
                                 :reagent [reagent "0.8.1"]
                                 :re-frame [re-frame "0.10.6"]
                                 :garden [garden "1.3.6"]
                                 :devcards [devcards "0.2.6"]}}})
