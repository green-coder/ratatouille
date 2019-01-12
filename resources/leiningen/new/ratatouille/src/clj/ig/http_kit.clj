(ns {{project.ns.name}}.ig.http-kit
  (:require [integrant.core :as ig]
            [org.httpkit.server :as http-kit]))

(defmethod ig/init-key :{{project.ns.name}}.ig/http-kit [_ {:keys [handler port]}]
  (let [server-stop (http-kit/run-server handler {:port port})]
    (println (format "http-kit server is now running on port %d." port))
    server-stop))

(defmethod ig/halt-key! :{{project.ns.name}}.ig/http-kit [_ server-stop]
  (server-stop)
  (println "http-kit server stopped."))
