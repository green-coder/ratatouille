(ns leiningen.new.selmer-util
  (:require [clojure.string :as str]
            [selmer.filters :as filters]))

(defn- meta->str [m]
  (case (count m)
    0 nil
    1 (let [entry (first m)]
        (str "^" (if (true? (second entry))
                   (first entry)
                   m)))
    (str "^" m)))

(defn multi-line [coll indent]
  (let [indent (filters/parse-number indent)]
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
                           "}"))))

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
