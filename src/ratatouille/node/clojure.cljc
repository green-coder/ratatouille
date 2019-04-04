(ns ratatouille.node.clojure
  (:require [leiningen.new.templates :refer [->files
                                             name-to-path
                                             multi-segment
                                             project-name
                                             sanitize-ns
                                             slurp-resource]]))

(def node
  {:keyword :clojure
   :name "+clj"
   :description "Uses Clojure."
   :dependencies []
   :context {:project {:source-paths ["src/clj"]
                       :dependencies [:clojure]
                       :profiles {:uberjar {:aot :all}}}
             :main {:clj
                    ^{:ctx [:project :ns :name]}
                    (fn [project-ns]
                      (let [namespace (str project-ns ".core")]
                        {:path (str "clj/" (name-to-path namespace) ".clj")
                         :ns {:name namespace
                              :gen-class true}}))}
             :user {:clj {:ns {:name "user"}}}}})
