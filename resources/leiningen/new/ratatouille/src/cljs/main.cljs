{{#clj.ns}}#ctx main.cljs.ns{{/clj.ns}}

;; Prints something in the browser's console.
(println "Hello, world from Clojurescript!")
{{#tag.reagent}}

;; State of the Reagent app.
(defonce app-state
  (ra/atom {:text "Hello world from Reagent!"
            :counter 0}))

(defn root-view-component []
  [:div
   [:h1 (:text @app-state)]
   [:p "Counter: " (:counter @app-state)]
   [:button {:on-click #(swap! app-state update-in [:counter] inc)}
            "+1"]
   [:h3 "Edit this in src/{{main.cljs.path}} and watch it change!"]])

(defn mount-app-element []
  (when-let [app-element (gdom/getElement "app")]
    (ra/render-component [root-view-component] app-element)))

(mount-app-element)

 ;; Reload hook, specified with the ^:after-load metadata.
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; Optionally touch your app-state to force a re-rendering, depending on your application's needs.
  (swap! app-state update-in [:__figwheel_reload_counter] inc))
{{/tag.reagent}}
