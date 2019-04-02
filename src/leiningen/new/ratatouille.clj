(ns leiningen.new.ratatouille
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-time.core :as t]
            [leiningen.core.eval :as eval]
            [leiningen.core.main :as main]
            [leiningen.new.selmer-util :as util]
            [leiningen.new.templates :refer [->files
                                             name-to-path
                                             multi-segment
                                             project-name
                                             sanitize-ns
                                             slurp-resource]]
            [selmer.filters :as filters]
            [selmer.parser :as parser :refer [render-file]]))

;; All the artifacts that Ratatouille is going to use are grouped here, making it easier to check for obsolescence.
(def latest-artifacts
  {:clojure '[org.clojure/clojure "1.10.0"]
   :clojurescript '[org.clojure/clojurescript "1.10.439"]
   :figwheel-main '[com.bhauman/figwheel-main "0.1.9"]
   :rebel-readline-cljs '[com.bhauman/rebel-readline-cljs "0.1.4"]
   :ancient '[lein-ancient "0.6.15"]
   :integrant '[integrant "0.7.0"]
   :integrant-repl '[integrant/repl "0.3.1"]
   :ring-core '[ring/ring-core "1.7.1"]
   :ring-json '[ring/ring-json "0.4.0"]
   :ring-defaults '[ring/ring-defaults "0.3.2"]
   :http-kit '[http-kit "2.3.0"]
   :reitit-core '[metosin/reitit-core "0.2.10"]
   :rum '[rum "0.11.3"]
   :reagent '[reagent "0.8.1"]
   :re-frame '[re-frame "0.10.6"]
   :garden '[garden "1.3.6"]
   :devcards '[devcards "0.2.6"]})

(def all-tags
  [{:keyword :readme
    :name "+readme"
    :description "Has a readme.md file."
    :dependencies []
    :context {}}

   {:keyword :git
    :name "+git"
    :description "Uses git, makes an initial commit."
    :dependencies []
    :context {}}

   {:keyword :ancient
    :name "+ancient"
    :description "Uses the lein-ancient plugin."
    :dependencies []
    :context {:project {:plugins ((juxt :ancient) latest-artifacts)}}}

   {:keyword :clojure
    :name "+clj"
    :description "Uses Clojure."
    :dependencies []
    :context {:project {:source-paths ["src/clj"]
                        :dependencies ((juxt :clojure) latest-artifacts)
                        :profiles {:uberjar {:aot :all}}}
              :main {:clj
                     ^{:ctx [:project :ns :name]}
                     (fn [project-ns]
                       (let [namespace (str project-ns ".core")]
                         {:path (str "clj/" (name-to-path namespace) ".clj")
                          :ns {:name namespace
                               :gen-class true}}))}
              :user {:clj {:ns {:name "user"}}}}}

   {:keyword :clojurescript
    :name "+cljs"
    :description "Uses Clojurescript via Figwheel Main."
    :dependencies []
    :context {:project {:source-paths ["src/cljs"]
                        :dependencies ((juxt :clojure :clojurescript) latest-artifacts)
                        :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
                                  "fig:dev"   ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
                                  "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]}
                                  ;"fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" hello-world.test-runner]}
                        :profiles {:dev {:dependencies ((juxt :figwheel-main :rebel-readline-cljs) latest-artifacts)}}}
              :main {:cljs
                     ^{:ctx [:project :ns :name]}
                      (fn [project-ns]
                        (let [namespace (str project-ns ".core")]
                          {:path (str "cljs/" (name-to-path namespace) ".cljs")
                           :ns {:name namespace
                                :meta {:figwheel-hooks true}}}))}}}

   {:keyword :integrant
    :name "+integrant"
    :description "Uses Integrant."
    :dependencies [:clojure]
    :context {:project {:dependencies ((juxt :integrant :integrant-repl) latest-artifacts)}
              :user {:clj {:ns {:require '[{:ns integrant.core
                                            :as ig}
                                           {:ns integrant.repl
                                            :refer [clear go halt prep init reset reset-all]}]}}}}}

   {:keyword :ring
    :name "+ring"
    :description "Ring and some middlewares."
    :dependencies [:clojure]
    :context {:project {:dependencies ((juxt :ring-core :ring-json :ring-defaults) latest-artifacts)}}}

   {:keyword :http-kit
    :name "+http-kit"
    :description "Uses Http-kit."
    :dependencies [:ring :integrant]
    :context {:project {:dependencies ((juxt :http-kit) latest-artifacts)}}}

   {:keyword :reitit
    :name "+reitit"
    :description "Uses Reitit."
    :dependencies [:http-kit :ring]
    :context {:project {:dependencies ((juxt :reitit-core) latest-artifacts)}}}

   {:keyword :rum
    :name "+rum"
    :description "Uses Rum."
    :dependencies [:clojurescript]
    :context {:project {:dependencies ((juxt :rum) latest-artifacts)}
              :main {:cljs {:ns {:require '[{:ns goog.dom
                                             :as gdom}
                                            {:ns rum.core
                                             :as rum}]}}}}}

   {:keyword :reagent
    :name "+reagent"
    :description "Uses Reagent."
    :dependencies [:clojurescript]
    :context {:project {:dependencies ((juxt :reagent) latest-artifacts)}
              :main {:cljs {:ns {:require '[{:ns goog.dom
                                             :as gdom}
                                            {:ns reagent.core
                                             :as ra}]}}}}}

   {:keyword :re-frame
    :name "+re-frame"
    :description "Uses Re-frame."
    :dependencies [:clojurescript]
    :context {:project {:dependencies ((juxt :re-frame) latest-artifacts)}
              :main {:cljs {:ns {:require '[{:ns goog.dom
                                             :as gdom}
                                            {:ns reagent.core
                                             :as ra}
                                            {:ns re-frame.core
                                             :as rf}]}}}}}

   {:keyword :garden
    :name "+garden"
    :description "Uses Garden, dynamically injects CSS from front end code."
    :dependencies [:clojurescript]
    :context {:project {:dependencies ((juxt :garden) latest-artifacts)}
              :main {:cljs {:ns {:require '[{:ns goog.style
                                             :as gs}
                                            {:ns garden.core
                                             :as gd}]}}}}}

   {:keyword :devcards
    :name "+devcards"
    :description "Uses Devcards for developing UI components in isolation from the rest of the app."
    :dependencies [:clojurescript]
    :context {:project {:dependencies ((juxt :devcards) latest-artifacts)
                        :aliases {"fig:devcards" ["trampoline" "run" "-m" "figwheel.main" "-b" "devcards"]}}
              :devcards {:cljs
                         ^{:ctx [:project :ns :name]}
                          (fn [project-ns]
                            (let [namespace (str project-ns ".devcards")]
                              {:path (str "cljs/" (name-to-path namespace) ".cljs")
                               :ns {:name namespace
                                    :require [{:ns (str project-ns ".core")}]
                                    :require-macros '[{:ns devcards.core
                                                       :as dc
                                                       :refer [defcard]}]}}))}
              :main {:cljs
                     ^:ctx (fn [ctx]
                             {:ns {:require '[{:ns devcards.core}]
                                   :require-macros (into []
                                                         (remove nil?)
                                                         [{:ns 'devcards.core
                                                           :as 'dc
                                                           :refer (into []
                                                                        (remove nil?)
                                                                        [(when (-> ctx :tag :clojurescript)
                                                                           'defcard)
                                                                         (when (-> ctx :tag :reagent)
                                                                           'defcard-rg)])}
                                                          (when (-> ctx :tag :rum)
                                                            '{:ns sablono.core
                                                              :as sab})])}})}}}])

   ;{:keyword :sente
   ; :name "+sente"
   ; :description "Uses Sente for real time communication between front end and back end."
   ; :dependencies [:clojure :clojurescript]
   ; :context {}}


(def tag-by-keyword
  (into {}
        (map (juxt :keyword identity))
        all-tags))

(def tag-by-name
  (into {}
        (map (juxt :name identity))
        all-tags))

(defn print-help-message []
  (let [options-str (str/join "\n" (mapv (fn [tag]
                                           (format "  %-11s %s"
                                                   (str (:name tag) ":")
                                                   (:description tag)))
                                       all-tags))]
    (-> (format "Usage:
  lein new ratatouille <project-name> <option>+

Options available:
%s

Example:
  lein new ratatouille my-app +readme +git +ancient +http-kit +rum +garden +devcards"
                options-str)
        main/info)))

(defn parse-options
  "Returns a set of tags or nil, eventually prints an error or help message."
  [project-name options]
  (cond
    ;; If the user wants to display the help message.
    (empty? options)
    (print-help-message)

    ;; Sanity check on all the options.
    (not-every? #(contains? tag-by-name %) options)
    (doseq [option options]
      (when-not (contains? tag-by-name option)
        (main/info (format "Option not supported: %s\n" option))))

    ;; Normal use case.
    :else
    (let [requested-tags (into #{}
                               (map (comp :keyword tag-by-name))
                               options)

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

(defn coll->true-map [coll]
  (into {}
        (map (fn [k] [k true]))
        coll))


(defn context-merge
  "Custom deep merge of 2 contexts."
  [prev-ctx next-ctx]
  (letfn [;; Walk in the data structure and apply f here and there.
          (walk [f val]
            (cond (map? val) (into {}
                                   (map (fn [[k v]]
                                          [k (walk f v)]))
                                   val)
                  (coll? val) (into (empty val)
                                    (map (fn [x]
                                           (walk f x)))
                                    val)
                  :else (f val)))

          ;; Handle the case of function annotated with meta ^:ctx
          (apply-to-context [x]
            (if (fn? x)
              (if-let [ctx-meta (:ctx (meta x))]
                (x (get-in prev-ctx (if (true? ctx-meta) [] ctx-meta)))
                x)
              x))

          ;; Custom deep merge of contexts
          (ctx-merge [val1 val2]
            (cond (nil? val1) val2

                  (map? val1)
                  (do (assert (map? val2) (prn-str val2))
                      (into val1
                            (map (fn [[k v]]
                                   [k (ctx-merge (val1 k) v)]))
                            val2))

                  (coll? val1)
                  (do (assert (and (coll? val2)
                                   (not (map? val2)))
                              (prn-str val2))
                      (into val1 val2))

                  :else val2))]
    (ctx-merge prev-ctx (walk apply-to-context next-ctx))))

(defn make-context [project-name options tags]
  (let [project-ns (sanitize-ns project-name)
        dep-comparator (fn [[x _] [y _]] ; orders the dependencies by their unqualified names
                         (compare (name x) (name y)))
        configs (into [{:tag (coll->true-map tags)}
                       {:project {:name project-name
                                  :options options
                                  :year (t/year (t/now))
                                  :ns {:name project-ns
                                       :path (name-to-path project-ns)}
                                  :dependencies (sorted-set-by dep-comparator)
                                  :plugins (sorted-set-by dep-comparator)}}]
                      (map (comp :context tag-by-keyword))
                      tags)
        config (reduce context-merge {} configs)]
    (-> config
        (update-in [:project :dependencies] #(into [] %))
        (update-in [:project :plugins] #(into [] %)))))

;(make-context "patate" [:clojurescript :reagent :garden])


;; Register the template files location in Selmer.
(parser/set-resource-path! (io/resource "leiningen/new/ratatouille/"))

;; Register some custom filters in Selmer.
(filters/add-filter! :multi-line util/multi-line)
(filters/add-filter! :clj-ns util/clj-ns)

(def <>-delimiters
  {:tag-open \<
   :tag-close \>})

(defn ratatouille [project-name & options]
  (let [tags (or (parse-options project-name options)
                 (main/exit 0 "No files were created."))
        context (make-context project-name options tags)
        files (concat
                (list ["project.clj" (render-file "project.clj" context)])
                (when (contains? tags :git)
                  (list [".gitignore" (render-file "gitignore" context)]))
                (when (contains? tags :readme)
                  (list ["README.md" (render-file "README.md" context)]))
                (when (contains? tags :clojure)
                  (list ["src/clj/user.clj" (render-file "src/clj/user.clj" context)]
                        ["src/{{main.clj.path}}" (render-file "src/clj/main.clj" context)]))
                (when (contains? tags :integrant)
                  (list ["resources/clj-config.edn" (render-file "resources/clj-config.edn" context <>-delimiters)]))
                (when (contains? tags :http-kit)
                  (list ["src/clj/{{project.ns.path}}/ig/http_kit.clj" (render-file "src/clj/ig/http_kit.clj" context)]
                        (if (contains? tags :reitit)
                          ["src/clj/{{project.ns.path}}/ig/reitit.clj" (render-file "src/clj/ig/reitit.clj" context <>-delimiters)]
                          ["src/clj/{{project.ns.path}}/ig/ring_handler.clj" (render-file "src/clj/ig/ring_handler.clj" context)])))
                (when (contains? tags :clojurescript)
                  (list ["figwheel-main.edn" (render-file "figwheel-main.edn" context)]
                        ["dev.cljs.edn" (render-file "dev.cljs.edn" context)]
                        ["resources/public/index.html" (render-file "resources/public/index.html" context)]
                        ["resources/public/css/style.css" (render-file "resources/public/css/style.css" context)]
                        ["dev/fig-launcher.clj" (render-file "dev/fig-launcher.clj" context)]
                        ["src/{{main.cljs.path}}" (render-file "src/cljs/main.cljs" context)]))
                (when (contains? tags :devcards)
                  (list ["devcards.cljs.edn" (render-file "devcards.cljs.edn" context)]
                        ["resources/public/devcards.html" (render-file "resources/public/devcards.html" context)]
                        ["src/{{devcards.cljs.path}}" (render-file "src/cljs/devcards.cljs" context)])))]
    (apply ->files
           (assoc context
             :name (-> context :project :name))
           files)

    (when (contains? tags :git)
      (binding [eval/*dir* (-> context :project :name)]
        (eval/sh-with-exit-code "Couldn't create git repo"
                                "git" "init")
        (eval/sh-with-exit-code "Couldn't add files to the repo"
                                "git" "add" ".")
        (eval/sh-with-exit-code "Couldn't make the initial commit"
                                "git" "commit" "-a" "-m" "Initial commit, only the code from the template.")))))
