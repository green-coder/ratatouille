{{#clj.ns}}#ctx main.cljs.ns{{/clj.ns}}

;; Prints something in the browser's console.
(enable-console-print!)
(println "Hello, world from Clojurescript!")

{{#tag.garden}}
;; Install some CSS style in the browser, dynamically.
(gs/installStyles (gd/css [:body {:background "#c0c0c0"}]))

{{/tag.garden}}
{{#tag.devcards}}
;; Defines a card.
(defcard my-data-card
  "The card's description."
  ["Hello, world!"
   #{1 2 3 4 5}
   {:a 0 :b 1 :c 2 :d 3 :e 4}])

{{/tag.devcards}}
{{#tag.rum}}
;; State of the counter
(defonce counter-state (atom 0))

;; A Rum component
(rum/defc counter < rum/reactive []
  [:div {:on-click (fn [_] (swap! counter-state inc))}
        "Clicks: " (rum/react counter-state)])

{{#tag.devcards}}
;; A devcard, to test the component above.
(defcard my-rum-card
  "The card's description."
  (sab/html (counter)))

{{/tag.devcards}}
(defn mount-app-element []
  (when-let [app-element (gdom/getElement "app")]
    (rum/mount (counter) app-element)))

 ;; Reload hook, specified with the ^:after-load metadata.
(defn ^:after-load on-reload []
  (mount-app-element))

;; This is run only once, on the first program load.
(defonce ^private first-run
  (do (mount-app-element) nil))

{{/tag.rum}}
{{#tag.reagent}}
;; State of the Reagent app.
(defonce app-state
  (ra/atom {:text "Hello world from Reagent!"
            :counter 0}))

;; A Reagent component
(defn counter [ratom]
  [:div
   [:p "Counter: " (:counter @ratom)]
   [:button {:on-click #(swap! ratom update-in [:counter] inc)}
            "+1"]])

{{#tag.devcards}}
;; A devcard, to test the component above.
(defcard-rg my-reagent-card
  "The card's description."
  (fn [ratom-data owner]
    [counter ratom-data]) ; The Reagent component to test
  {:counter 20} ; Reagent atom
  {:inspect-data true
   :history true})

{{/tag.devcards}}
(defn root-view-component []
  [:div
   [:h1 (:text @app-state)]
   [counter app-state]
   [:h3 "Edit this in src/{{main.cljs.path}} and watch it change!"]])

(defn mount-app-element []
  (when-let [app-element (gdom/getElement "app")]
    (ra/render-component [root-view-component] app-element)))

 ;; Reload hook, specified with the ^:after-load metadata.
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; Optionally touch your app-state to force a re-rendering, depending on your application's needs.
  (swap! app-state update-in [:figwheel/reload-counter] inc))

;; This is run only once, on the first program load.
(defonce ^:private first-run
  (do (mount-app-element) nil))

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

(defonce ^:private first-run
  ;; This is run only once, on the first program load.
  (do
    (rf/dispatch-sync [:event/initialize-db])
    (mount-app-element)
    nil))

{{/tag.re-frame}}
