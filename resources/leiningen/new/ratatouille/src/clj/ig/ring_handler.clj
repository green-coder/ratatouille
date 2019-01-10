  (ns {{project.ns.name}}.ig.ring-handler
    (:require [integrant.core :as ig]))

(defmethod ig/init-key :{{project.ns.name}}.ig/ring-handler [_ {:keys [message]}]
  (fn [request]
    {:status 200
     :headers {"Content-Type" "text/plain; charset=utf-8"}
     :body message}))
