(ns leiningen.new.stencil-util
  (:require [clojure.string :as str]
            [leiningen.new.templates :as templates]))

(defn- string-filter [f]
  ^:stencil/pass-render
  (fn [s ctx render]
    (f (render s ctx))))

(def context
  {:str {:cap (string-filter str/capitalize)
         :upper (string-filter str/upper-case)
         :lower (string-filter str/lower-case)
         :path (string-filter templates/name-to-path)}})
