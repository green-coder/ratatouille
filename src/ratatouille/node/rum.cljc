(ns ratatouille.node.rum)

(def node
  {:keyword :rum
   :name "+rum"
   :description "Uses Rum."
   :dependencies [:clojurescript]
   :context {:project {:dependencies [:rum]}
             :main {:cljs {:ns {:require '[{:ns goog.dom
                                            :as gdom}
                                           {:ns rum.core
                                            :as rum}]}}}}})

