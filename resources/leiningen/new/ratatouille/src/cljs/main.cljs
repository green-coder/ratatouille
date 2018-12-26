{{#clj.ns}}#ctx main.cljs.ns{{/clj.ns}}

;; Prints something in the browser's console.
(enable-console-print!)
(println "Hello, world from Clojurescript!")
{{#tag.garden}}

;; Install some CSS style in the browser, dynamically.
(gs/installStyles (gd/css [:body {:background "#c0c0c0"}]))
{{/tag.garden}}
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

 ;; Reload hook, specified with the ^:after-load metadata.
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; Optionally touch your app-state to force a re-rendering, depending on your application's needs.
  (swap! app-state update-in [:__figwheel_reload_counter] inc))

;; This is run only once, on the first program load.
(defonce _ (mount-app-element))
{{/tag.reagent}}
{{#tag.re-frame}}

;; The initial state of the app.
(def initial-db
  {:text "Hello world from Reagent!"
   :counter 0})

;; Register some events

(rf/reg-event-db
  :event/initialize-db
  (fn [_ _]
    initial-db))

(rf/reg-event-db
  :event/inc-counter
  (fn [db _]
    (update-in db [:counter] inc)))

;; Register some subscriptions

(rf/reg-sub
  :sub/text
  (fn [db]
    (:text db)))

(rf/reg-sub
  :sub/counter
  (fn [db]
    (:counter db)))


(defn root-view-component []
  (let [text (rf/subscribe [:sub/text])
        counter (rf/subscribe [:sub/counter])]
    [:div
     [:h1 @text]
     [:p "Counter: " @counter]
     [:button {:on-click #(rf/dispatch [:event/inc-counter])}
              "+1"]
     [:h3 "Edit this in src/{{main.cljs.path}} and watch it change!"]]))

(defn mount-app-element []
  (rf/clear-subscription-cache!)
  (when-let [app-element (gdom/getElement "app")]
    (ra/render [root-view-component] app-element)))

;; Reload hook, specified with the ^:after-load metadata.
(defn ^:after-load on-reload []
  (mount-app-element))

(defonce _
  ;; This is run only once, on the first program load.
  (do
    (rf/dispatch-sync [:event/initialize-db])
    (mount-app-element)))
{{/tag.re-frame}}
