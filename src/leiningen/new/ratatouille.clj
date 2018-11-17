(ns leiningen.new.ratatouille
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "ratatouille"))

(defn ratatouille [name]
  (let [data {:name name
              :sanitized (name-to-path name)}]
    (main/info "Preparing a ratatouille of libraries and configs for your new project.")
    (->files data
             ["src/{{sanitized}}/foo.clj" (render "foo.clj" data)])))
