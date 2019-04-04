(ns ratatouille.node.reitit)

(def node
  {:keyword :reitit
   :name "+reitit"
   :description "Uses Reitit."
   :dependencies [:http-kit :ring]
   :context {:project {:dependencies [:reitit-core]}}})
