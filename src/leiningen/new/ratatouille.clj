(ns leiningen.new.ratatouille
  (:require [clj-time.core :as t]
            [leiningen.core.main :as main]
            [leiningen.new.stencil-util :as stencil-util]
            [leiningen.new.templates :refer [->files
                                             name-to-path
                                             multi-segment
                                             project-name
                                             render-text
                                             renderer
                                             sanitize-ns
                                             slurp-resource]]))

;; All the artifacts that Ratatouille is going to use are grouped here, making it easier to check for obsolescence.
(def latest-artifacts
  {:clojure '[org.clojure/clojure "1.10.0"]
   :clojurescript '[org.clojure/clojurescript "1.10.439"]
   :figwheel-main '[com.bhauman/figwheel-main "0.1.9"]
   :rebel-readline-cljs '[com.bhauman/rebel-readline-cljs "0.1.4"]
   :reagent '[reagent "0.8.1"]
   :re-frame '[re-frame "0.10.6"]})

(def all-tags
  [{:keyword :git
    :names ["git"]
    :description "Uses git."
    :dependencies []
    :context {}}

   {:keyword :readme
    :names ["readme"]
    :description "Has a readme.md file."
    :dependencies []
    :context {}}

   {:keyword :clojure
    :names ["clojure" "clj"]
    :description "Uses Clojure."
    :dependencies []
    :context {:project {:source-paths ["src/clj"]
                        :dependencies ((juxt :clojure) latest-artifacts)}}}

   {:keyword :clojurescript
    :names ["clojurescript" "cljs"]
    :description "Uses Clojurescript via Figwheel Main."
    :dependencies []
    :context {:project {:source-paths ["src/cljs"]
                        :dependencies ((juxt :clojure :clojurescript) latest-artifacts)
                        :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
                                  "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
                                  "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]}
                                  ;"fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" hello-world.test-runner]}
                        :profiles {:dev {:dependencies ((juxt :figwheel-main :rebel-readline-cljs) latest-artifacts)}}}}}
   {:keyword :default
    :names ["default"]
    :description "Is included when to tags are specified, implies some commonly used tags for a Clojure project."
    :dependencies [:git :readme :clojure]
    :context {}}

   {:keyword :reagent
    :names ["reagent"]
    :description "Uses Reagent."
    :dependencies [:clojurescript]
    :context {:project {:dependencies ((juxt :reagent) latest-artifacts)}
              :main {:cljs {:ns {:require '[{:ns goog.dom
                                             :as gdom}
                                            {:ns reagent.core
                                             :as ra}]}}}}}

   {:keyword :re-frame
    :names ["re-frame"]
    :description "Uses Re-frame."
    :dependencies [:clojurescript]
    :context {:project {:dependencies ((juxt :re-frame) latest-artifacts)}}}

   {:keyword :garden
    :names ["garden"]
    :description "Uses garden."
    :dependencies [:clojurescript]
    :context {}}

   {:keyword :sente
    :names ["sente"]
    :description "Uses Sente for real time communication between front end and back end."
    :dependencies [:clojure :clojurescript]
    :context {}}

   {:keyword :integrant
    :names ["integrant"]
    :description "Uses Integrant."
    :dependencies [:clojure]
    :context {}}

   {:keyword :front-end
    :names ["front-end"]
    :description "Includes default tags for front end development."
    :dependencies [:git :readme :reagent]
    :context {}}

   {:keyword :back-end
    :names ["back-end"]
    :description "Includes default tags for back end development."
    :dependencies [:git :readme :clojure]
    :context {}}

   {:keyword :fullstack
    :names ["fullstack"]
    :description "Includes default tags for fullstack development."
    :dependencies [:git :readme :reagent :clojure]
    :context {}}])

(defn unknown-tag [option]
  {:names [option]
   :description "[Unknown tag]"})


(def tag-by-keyword
  (into {}
        (map (juxt :keyword identity))
        all-tags))

(def tag-by-name
  (into {}
        (mapcat (fn [tag]
                  (map (fn [name] [name tag])
                       (:names tag))))
        all-tags))

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
      (doseq [tag all-tags]
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

          required-tags (into #{}
                              (mapcat (fn [tag]
                                        (tree-seq (constantly true)
                                                  (comp :dependencies tag-by-keyword)
                                                  tag)))
                              requested-tags)

          implied-tags (clojure.set/difference required-tags requested-tags)

          ;; Returns the tags in the same order as they are defined.
          tag-sorted-into (fn [dst tag-set]
                            (into dst
                                  (comp (map :keyword)
                                        (filter tag-set))
                                  all-tags))]

      (main/info (str "Requested tags: " (tag-sorted-into [] requested-tags)))
      (main/info (str "Implied tags: " (tag-sorted-into [] implied-tags)))

      (tag-sorted-into (sorted-set) required-tags))))

(def render (renderer "ratatouille"))

(defn coll->map [coll]
  (into {} (map (fn [k] [k true])) coll))

(defn deep-merge-with [f & vals]
  (if (not-every? map? vals)
    (reduce f vals)
    (apply merge-with (partial deep-merge-with f) vals)))

(defn into-dedup [coll1 coll2]
  (let [s (set coll1)]
    (into coll1 (remove s) coll2)))

(defn ratatouille [project-name & options]
  (let [tags (or (parse-options project-name options)
                 (main/exit 0 "No files were created."))
        context (apply deep-merge-with into-dedup
                       stencil-util/context
                       {:tag (coll->map tags)
                        :project {:name project-name
                                  :year (t/year (t/now))
                                  :source-paths []
                                  :dependencies []
                                  :aliases {}
                                  :profiles {:dev {:dependencies []}}}
                        :main (let [namespace (multi-segment (sanitize-ns project-name))
                                    path (name-to-path namespace)]
                                {:clj {:path (str "clj/" path ".clj")
                                       :ns {:name namespace
                                            :gen-class true}}
                                 :cljs {:path (str "cljs/" path ".cljs")
                                        :ns {:name namespace}}})}
                       (mapv (comp :context tag-by-keyword) tags))
        files (concat
                (list ["project.clj" (render "project.clj" context)])
                (when (contains? tags :git)
                  (list [".gitignore" (render ".gitignore" context)]))
                (when (contains? tags :readme)
                  (list ["README.md" (render "README.md" context)]))
                (when (contains? tags :clojure)
                  (list ["src/{{main.clj.path}}" (render "src/clj/main.clj" context)]))
                (when (contains? tags :clojurescript)
                  (list ["dev.cljs.edn" (render "dev.cljs.edn" context)]
                        ["figwheel-main.edn" (render "figwheel-main.edn" context)]
                        ["resources/public/index.html" (render "resources/public/index.html" context)]
                        ["resources/public/css/style.css" (render "resources/public/css/style.css" context)]
                        ["src/{{main.cljs.path}}" (render "src/cljs/main.cljs" context)])))]
    (apply ->files
           (assoc context
             :name (-> context :project :name))
           files)))
