(ns ratatouille.node.ring)

(def node
  {:keyword :ring
   :name "+ring"
   :description "Ring and some middlewares."
   :dependencies [:clojure]
   :context {:project {:dependencies [:ring-core :ring-json :ring-defaults]}}})
