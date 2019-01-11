{{user.clj.ns|clj-ns}}

(prn "src/clj/user.clj is running.")

{% if tag.integrant %}(defn load-config []
  (-> "clj-config.edn"
      clojure.java.io/resource
      slurp
      ig/read-string))

(integrant.repl/set-prep! (fn []
                            (let [config (load-config)]
                              (ig/load-namespaces config)
                              (ig/prep config))))

(comment
  ;; integrant-repl commands
  (init)
  (prep)
  (go)    ; equivalent of (init) then (prep)
  (halt)
  (reset) ; halts the system, reloads the source files, then restarts the system.
  _)
{% endif %}