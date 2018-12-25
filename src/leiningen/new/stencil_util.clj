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

(defn- fn-apply [f]
  ^:stencil/pass-render
  (fn [s ctx render]
    (let [context-data-reader (fn [val]
                                (let [path (mapv keyword (str/split (str val) #"\."))]
                                  (get-in ctx path)))
          args (read-whole-string {:readers {'ctx context-data-reader}} s)
          f-meta (meta f)]
      (cond
        (contains? f-meta :stencil/pass-render) (apply f ctx render args)
        (contains? f-meta :stencil/pass-context) (apply f ctx args)
        :else (apply f args)))))

(defn clj-ns [{:keys [name require gen-class] :as namespace}]
  (let [require-str (str/join "\n            "
                              (map (fn [{:keys [ns as refer]}]
                                     (str "["
                                          ns
                                          (when as (str " :as " as))
                                          (when refer (str " :refer " refer))
                                          "]"))
                                   require))]
    (str "(ns " name
         (when (seq require)
           (str "\n  (:require " require-str ")"))
         (when gen-class
           "\n  (:gen-class)")
         ")")))

(def context
  {:str {:cap (string-filter str/capitalize)
         :upper (string-filter str/upper-case)
         :lower (string-filter str/lower-case)
         :path (string-filter templates/name-to-path)}
   :clj {:ns (fn-apply clj-ns)}})
