(ns super-koalio.core
  (:require [play-cljs.core :as p]
            [super-koalio.state :as s]))

(declare game)

(def main-screen
  (reify p/Screen
    (on-show [_ state]
      (p/reset-state (s/initial-state "koalio.png" 18 26)))
    (on-hide [_ state])
    (on-render [_ {:keys [x y] :as state} total-time delta-time]
      [(assoc (:stand-right state) :x x :y y)
       (-> state
           (s/move game delta-time)
           (p/reset-state))])
    (on-event [_ state event])))

(defonce renderer
  (p/create-renderer 500 500 {:view (.querySelector js/document "#canvas")
                              :background-color 0x65C25D}))

(defonce game
  (doto (p/create-game renderer)
    (p/start ["keydown"])
    (p/set-screens [main-screen])))

