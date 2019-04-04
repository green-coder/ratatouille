(ns ratatouille.node.integrant)

(def node
  {:keyword :integrant
   :name "+integrant"
   :description "Uses Integrant."
   :dependencies [:clojure]
   :context {:project {:dependencies [:integrant :integrant-repl]}
             :user {:clj {:ns {:require '[{:ns integrant.core
                                           :as ig}
                                          {:ns integrant.repl
                                           :refer [clear go halt prep init reset reset-all]}]}}}}})
