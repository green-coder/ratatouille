(ns ratatouille.node.garden)

(def node
  {:keyword :garden
   :name "+garden"
   :description "Uses Garden, dynamically injects CSS from front end code."
   :dependencies [:clojurescript]
   :context {:project {:dependencies [:garden]}
             :main {:cljs {:ns {:require '[{:ns goog.style
                                            :as gs}
                                           {:ns garden.core
                                            :as gd}]}}}}})
