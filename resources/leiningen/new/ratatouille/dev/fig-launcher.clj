(ns fig-launcher
  (:require [figwheel.main.api :as fig]))

;; This file is for Cursive users, to launch Figwheel Main and have the Cursive's REPL hooked to the running CLJS program.
;; For more info, see https://github.com/bhauman/figwheel-main/blob/master/docs/docs/cursive.md}
(prn "fig-launcher.clj is running.")
(fig/start "dev")
