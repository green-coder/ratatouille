(ns ratatouille.node.devcards
  (:require [leiningen.new.templates :refer [->files
                                             name-to-path
                                             multi-segment
                                             project-name
                                             sanitize-ns
                                             slurp-resource]]))

(def node
  {:keyword :devcards$
   :name "+devcards"
   :description "Uses Devcards for developing UI components in isolation from the rest of the app."
   :dependencies [:clojurescript]
   :context {:project {:dependencies [:devcards]
                       :aliases {"fig:devcards" ["trampoline" "run" "-m" "figwheel.main" "-b" "devcards"]}}
             :devcards {:cljs
                        ^{:ctx [:project :ns :name]}
                         (fn [project-ns]
                           (let [namespace (str project-ns ".devcards")]
                             {:path (str "cljs/" (name-to-path namespace) ".cljs")
                              :ns {:name namespace
                                   :require [{:ns (str project-ns ".core")}]
                                   :require-macros '[{:ns devcards.core
                                                      :as dc
                                                      :refer [defcard]}]}}))}
             :main {:cljs
                    ^:ctx (fn [ctx]
                            {:ns {:require '[{:ns devcards.core}]
                                  :require-macros (into []
                                                        (remove nil?)
                                                        [{:ns 'devcards.core
                                                          :as 'dc
                                                          :refer (into []
                                                                       (remove nil?)
                                                                       [(when (-> ctx :tag :clojurescript)
                                                                          'defcard)
                                                                        (when (-> ctx :tag :reagent)
                                                                          'defcard-rg)])}
                                                         (when (-> ctx :tag :rum)
                                                           '{:ns sablono.core
                                                             :as sab})])}})}}})
