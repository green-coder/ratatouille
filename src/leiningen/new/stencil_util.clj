(ns leiningen.new.stencil-util
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [leiningen.new.templates :as templates]))

(defn- string-filter [f]
  ^:stencil/pass-render
  (fn [s ctx render]
    (f (render s ctx))))

(defn- read-whole-string
  ([s] (edn/read-string (str "[" s "]")))
  ([opts s] (edn/read-string opts (str "[" s "]"))))

(def clj-ns
  ^:stencil/pass-context
  (fn [s ctx]
    (let [[file-path :as args] (read-whole-string s)
          file (get-in ctx file-path)
          {:keys [name require]} (:ns file)
          require-str (str/join "\n            "
                            (map (fn [{:keys [ns as refer]}]
                                   (str "["
                                        ns
                                        (when as (str " :as " as))
                                        (when refer (str " :refer " refer))
                                        "]"))
                                 require))]
      (str "(ns " name "\n"
           "  (:require " require-str "))"))))

(def context
  {:str {:cap (string-filter str/capitalize)
         :upper (string-filter str/upper-case)
         :lower (string-filter str/lower-case)
         :path (string-filter templates/name-to-path)}
   :clj {:ns clj-ns}})
