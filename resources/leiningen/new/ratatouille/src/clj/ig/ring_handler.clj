(ns {{project.ns.name}}.ig.ring-handler
  (:require [integrant.core :as ig]
            [ring.util.response :refer [response charset]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn- my-handler [message]
  (fn [request]
    ;{:status 200
    ; :headers {"Content-Type" "text/plain; charset=utf-8"}
    ; :body message}
    (-> (response message)
        (charset "utf-8"))))

(defmethod ig/init-key :{{project.ns.name}}.ig/ring-handler [_ {:keys [message]}]
  (-> (my-handler message)
      (wrap-defaults site-defaults)))
