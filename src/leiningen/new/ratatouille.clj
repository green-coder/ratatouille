(ns leiningen.new.ratatouille
  (:require [leiningen.core.main :as main]
            [leiningen.new.stencil-util :as stencil-util]
            [leiningen.new.templates :refer [->files
                                             name-to-path
                                             multi-segment
                                             project-name
                                             render-text
                                             renderer
                                             sanitize-ns
                                             slurp-resource]]))

(def tags
  [{:keyword :git
    :names ["git"]
    :description "Uses git."
    :dependencies []}

   {:keyword :readme
    :names ["readme"]
    :description "Has a readme.md file."
    :dependencies []}

   {:keyword :clojure
    :names ["clojure" "clj"]
    :description "Uses Clojure."
    :dependencies []}

   {:keyword :clojurescript
    :names ["clojurescript" "cljs"]
    :description "Uses Clojurescript."
    :dependencies []}

   {:keyword :default
    :names ["default"]
    :description "Is included when to tags are specified, implies some commonly used tags for a Clojure project."
    :dependencies [:git :readme :clojure]}

   {:keyword :reagent
    :names ["reagent"]
    :description "Uses Reagent."
    :dependencies [:clojurescript]}

   {:keyword :re-frame
    :names ["re-frame"]
    :description "Uses Re-frame."
    :dependencies [:reagent]}

   {:keyword :re-frame+sample
    :names ["re-frame+sample"]
    :description "Uses Re-frame with some sample code and data."
    :dependencies [:re-frame]}

   {:keyword :sente
    :names ["sente"]
    :description "Uses Sente for real time communication between front end and back end."
    :dependencies [:clojure :clojurescript]}

   {:keyword :sente+sample
    :names ["sente+sample"]
    :description "Uses Sente for real time communication between front end and back end."
    :dependencies [:sente]}

   {:keyword :integrant
    :names ["integrant"]
    :description "Uses Integrant."
    :dependencies [:clojure]}

   {:keyword :integrant+sample
    :names ["integrant+sample"]
    :description "Uses Integrant with some sample code and data."
    :dependencies [:integrant]}

   {:keyword :front-end
    :names ["front-end"]
    :description "Includes default tags for front end development."
    :dependencies [:git :readme :reagent]}

   {:keyword :back-end
    :names ["back-end"]
    :description "Includes default tags for back end development."
    :dependencies [:git :readme :clojure]}

   {:keyword :fullstack
    :names ["fullstack"]
    :description "Includes default tags for fullstack development."
    :dependencies [:git :readme :reagent :sente]}])

(defn unknown-tag [option]
  {:names [option]
   :description "[Unknown tag]"})


(def tag-by-keyword
  (into {}
        (map (juxt :keyword identity))
        tags))

(def tag-by-name
  (into {}
        (mapcat (fn [tag]
                  (map (fn [name] [name tag])
                       (:names tag))))
        tags))

(defn print-tag-help! [tag]
  (main/info (format "%s:\n  %s\n"
                     (pr-str (let [names (:names tag)]
                                (if (= (count names) 1)
                                  (first names)
                                  names)))
                     (:description tag))))

;(print-tag-help! (:clojure tag-by-keyword))
;(print-tag-help! (:git tag-by-keyword))

(defn parse-options
  "Returns a set of tags or nil, eventually prints an error or help message."
  [project-name options]
  (cond
    ;; If the user wants to display some help messages.
    (= project-name "help")
    (cond
      (empty? options)
      (doseq [tag tags]
        (print-tag-help! tag))

      :else
      (doseq [option options]
        (-> (tag-by-name option (unknown-tag option))
            print-tag-help!)))

    ;; Sanity check on all the options.
    (not-every? #(contains? tag-by-name %) options)
    (doseq [option options]
      (when-not (contains? tag-by-name option)
        (main/info (str "Option not supported: " option))))

    ;; Normal use case.
    :else
    (let [;; Use default tags if no options are provided.
          options (or options ["default"])

          requested-tags (into #{} (map (comp :keyword tag-by-name)) options)

          all-tags (into #{}
                         (mapcat (fn [tag]
                                   (tree-seq (constantly true)
                                             (comp :dependencies tag-by-keyword)
                                             tag)))
                         requested-tags)
          implied-tags (clojure.set/difference all-tags requested-tags)]

      (main/info (str "Requested tags: " (vec requested-tags)))
      (main/info (str "Implied tags: " (vec (sort implied-tags))))
      all-tags)))

(def render (renderer "ratatouille"))

(defn ratatouille [project-name & options]
  (let [tags (or (parse-options project-name options)
                 (main/exit 0 "No files were created."))
        context (merge stencil-util/context
                       {;:tag (vec tags)
                        :project {:name project-name}
                        :core (let [namespace (multi-segment (sanitize-ns project-name))]
                                {:namespace namespace
                                 :path (name-to-path namespace)})})
        files (concat
                (list ["project.clj" (render "project.clj" context)])
                (when (contains? tags :git)
                  (list [".gitignore" (render ".gitignore" context)]))
                (when (contains? tags :readme)
                  (list ["README.md" (render "README.md" context)]))
                (when (contains? tags :clojure)
                  (list ["src/clj/{{core.path}}.clj" (render "src/clj/core.clj" context)]))
                (when (contains? tags :clojurescript)
                  (list ["src/cljs/{{core.path}}.cljs" (render "src/cljs/core.cljs" context)])))]
    (apply ->files
           (assoc context
             :name (-> context :project :name))
           files)))
