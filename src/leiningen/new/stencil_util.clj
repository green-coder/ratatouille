(ns leiningen.new.stencil-util)

(defn- string-filter [f]
  ^:stencil/pass-render
  (fn [s ctx render]
    (f (render s ctx))))

(def context
  {:str {:cap (string-filter clojure.string/capitalize)
         :upper (string-filter clojure.string/upper-case)
         :lower (string-filter clojure.string/lower-case)
         :path (string-filter leiningen.new.templates/name-to-path)}})
