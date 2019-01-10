{{#clj.ns}}#ctx main.clj.ns{{/clj.ns}}

{{#tag.integrant}}
(def config
  (-> "clj-config.edn"
      clojure.java.io/resource
      slurp
      ig/read-string))

(ig/load-namespaces config)

(comment
  (def system (-> config ig/prep ig/init))
  (ig/halt! system)
  _)
{{/tag.integrant}}

(defn -main [& args]
  (println "Hello, world!"))
