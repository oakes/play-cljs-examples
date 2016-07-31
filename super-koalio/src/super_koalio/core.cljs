(ns super-koalio.core
  (:require [play-cljs.core :as p]
            [super-koalio.state :as s]))

(declare game)

(def main-screen
  (reify p/Screen
    (on-show [_ state]
      (p/reset-state (s/initial-state)))
    (on-hide [_ state])
    (on-render [_ {:keys [x y] :as state}]
      [(assoc (:current state) :x x :y y)
       (-> state
           (s/move game)
           (s/prevent-move game)
           (s/animate)
           (p/reset-state))])
    (on-event [_ state event])))

(def canvas (.querySelector js/document "#canvas"))

(defonce renderer
  (p/create-renderer 500 500 {:view canvas
                              :background-color 0x8080FF}))

(defonce game (p/create-game renderer))

(doto game
  (p/stop)
  (p/start ["keydown"])
  (p/set-screens [main-screen]))

