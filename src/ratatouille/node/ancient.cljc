(ns ratatouille.node.ancient)

(def node
  {:keyword :ancient
   :name "+ancient"
   :description "Uses the lein-ancient plugin."
   :dependencies []
   :context {:project {:plugins [:ancient]}}})
