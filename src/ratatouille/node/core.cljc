(ns ratatouille.node.core
  (:require [ratatouille.node.ancient       :as ancient]
            [ratatouille.node.citrus        :as citrus]
            [ratatouille.node.clojure       :as clojure]
            [ratatouille.node.clojurescript :as clojurescript]
            [ratatouille.node.devcards      :as devcards]
            [ratatouille.node.garden        :as garden]
            [ratatouille.node.git           :as git]
            [ratatouille.node.http-kit      :as http-kit]
            [ratatouille.node.integrant     :as integrant]
            [ratatouille.node.leiningen     :as leiningen]
            [ratatouille.node.re-frame      :as re-frame]
            [ratatouille.node.readme        :as readme]
            [ratatouille.node.reagent       :as reagent]
            [ratatouille.node.reitit        :as reitit]
            [ratatouille.node.ring          :as ring]
            [ratatouille.node.rum           :as rum]
            [ratatouille.node.sente         :as sente]))

(def all-nodes
  [ancient/node
   clojure/node
   clojurescript/node
   devcards/node
   garden/node
   git/node
   http-kit/node
   integrant/node
   re-frame/node
   readme/node
   reagent/node
   reitit/node
   ring/node
   rum/node])
