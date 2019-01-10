(ns {{project.ns.name}}.ig.http-kit
  (:require [integrant.core :as ig]
            [org.httpkit.server :as http-kit]))

(defmethod ig/init-key :{{project.ns.name}}.ig/http-kit [_ {:keys [handler port]}]
  (http-kit/run-server handler {:port port}))

(defmethod ig/halt-key! :{{project.ns.name}}.ig/http-kit [_ server-stop]
  (server-stop))
