(ns ratatouille.node.http-kit)

(def node
  {:keyword :http-kit
   :name "+http-kit"
   :description "Uses Http-kit."
   :dependencies [:ring :integrant]
   :context {:project {:dependencies [:http-kit]}}})
