(ns <{project.ns.name}>.ig.reitit
  (:require [integrant.core :as ig]
            [reitit.core :as r]
            [ring.util.response :refer [response charset]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn ping [_]
  (-> (response "pong")
      (charset "utf-8")))

(defn user-info [{:keys [id]}]
  (response {:name (str "Mr. " id)
             :age 42}))

(def router
  (r/router [["/api/ping" :api/ping]
             ["/api/user/:id" :api/user]]))

(def handler-map
  {:api/ping ping
   :api/user (wrap-json-response user-info)})

(defn dispatch [request]
  (let [{{handler-key :name} :data, params :path-params} (r/match-by-path router (:uri request))]
    (when-let [handler (handler-map handler-key)]
      (handler params))))

(defmethod ig/init-key :<{project.ns.name}>.ig/reitit [_ _]
  (-> dispatch
      (wrap-defaults site-defaults)))
