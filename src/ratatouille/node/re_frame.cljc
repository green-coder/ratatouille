(ns ratatouille.node.re-frame)

(def node
  {:keyword :re-frame
   :name "+re-frame"
   :description "Uses Re-frame."
   :dependencies [:clojurescript]
   :context {:project {:dependencies [:re-frame]}
             :main {:cljs {:ns {:require '[{:ns goog.dom
                                            :as gdom}
                                           {:ns reagent.core
                                            :as ra}
                                           {:ns re-frame.core
                                            :as rf}]}}}}})

