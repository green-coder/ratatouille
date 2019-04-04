(ns ratatouille.node.reagent)


(def node
  {:keyword :reagent
   :name "+reagent"
   :description "Uses Reagent."
   :dependencies [:clojurescript]
   :context {:project {:dependencies [:reagent]}
             :main {:cljs {:ns {:require '[{:ns goog.dom
                                            :as gdom}
                                           {:ns reagent.core
                                            :as ra}]}}}}})
