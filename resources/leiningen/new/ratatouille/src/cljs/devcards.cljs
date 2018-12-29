{{#clj.ns}}#ctx devcards.cljs.ns{{/clj.ns}}

;; Defines a card.
(defcard hello-devcards
  #{1 2 3})

;; Shows the UI.
(dc/start-devcard-ui!)
