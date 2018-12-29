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

(defn- meta->str [m]
  (case (count m)
    0 nil
    1 (let [entry (first m)]
        (str "^" (if (true? (second entry))
                   (first entry)
                   m)))
    (str "^" m)))

(defn multiline-coll [indent coll]
  (cond (vector? coll) (str "["
                            (str/join (apply str "\n" (repeat (+ indent 1) " "))
                                      coll)
                            "]")
        (list? coll) (str "("
                          (str/join (apply str "\n" (repeat (+ indent 1) " "))
                                    coll)
                          ")")
        (set? coll) (str "#{"
                         (str/join (apply str "\n" (repeat (+ indent 2) " "))
                                   coll)
                         "}")
        (map? coll) (str "{"
                         (str/join (apply str "\n" (repeat (+ indent 1) " "))
                                   (mapv (fn [[k v]] (pr-str k v)) coll))
                         "}")))

(defn clj-ns [{:keys [meta name require require-macros gen-class] :as namespace}]
  (let [require-entries
        (fn [entries]
          (str/join "\n            "
                    (map (fn [{:keys [ns as refer]}]
                           (str "["
                                ns
                                (when as (str " :as " as))
                                (when refer (str " :refer " refer))
                                "]"))
                         entries)))]
    (str "(ns "
         (when meta
           (str (meta->str meta) " "))
         name
         (when (seq require)
           (str "\n  (:require " (require-entries require) ")"))
         (when (seq require-macros)
           (str "\n  (:require-macros " (require-entries require-macros) ")"))
         (when gen-class
           "\n  (:gen-class)")
         ")")))

(def context
  {:str {:cap (string-filter str/capitalize)
         :upper (string-filter str/upper-case)
         :lower (string-filter str/lower-case)
         :path (string-filter templates/name-to-path)
         :multiline (fn-apply multiline-coll)}
   :clj {:ns (fn-apply clj-ns)}})
