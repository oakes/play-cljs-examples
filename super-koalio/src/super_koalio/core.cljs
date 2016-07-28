(ns super-koalio.core
  (:require [play-cljs.core :as p]))

(def ^:const url "koalio.png")
(def ^:const tile-width 18)
(def ^:const tile-height 26)

(def main-screen
  (reify p/Screen
    (on-show [_ state]
      (let [stand-right (p/sprite url 0 0 {:frame (p/rectangle 0 0 tile-width tile-height)})
            stand-left (-> stand-right (assoc :anchor [1 0]) (assoc :scale [-1 1]))
            jump-right (p/sprite url 0 0 {:frame (p/rectangle tile-width 0 tile-width tile-height)})
            jump-left (-> jump-right (assoc :anchor [1 0]) (assoc :scale [-1 1]))
            walk1-right (p/sprite url 0 0 {:frame (p/rectangle (* 2 tile-width) 0 tile-width tile-height)})
            walk1-left (-> walk1-right (assoc :anchor [1 0]) (assoc :scale [-1 1]))
            walk2-right (p/sprite url 0 0 {:frame (p/rectangle (* 3 tile-width) 0 tile-width tile-height)})
            walk2-left (-> walk2-right (assoc :anchor [1 0]) (assoc :scale [-1 1]))
            walk3-right (p/sprite url 0 0 {:frame (p/rectangle (* 4 tile-width) 0 tile-width tile-height)})
            walk3-left (-> walk3-right (assoc :anchor [1 0]) (assoc :scale [-1 1]))]
        (p/reset-state {:stand-right stand-right
                        :stand-left stand-left
                        :jump-right jump-right
                        :jump-left jump-left
                        :walk-right [walk1-right walk2-right walk3-right]
                        :walk-left [walk1-left walk2-left walk3-left]
                        :x-velocity 0
                        :y-velocity 0
                        :x 0
                        :y 0
                        :can-jump? false
                        :direction :right})))
    (on-hide [_ state])
    (on-render [_ state timestamp]
      (:stand-right state))
    (on-event [_ state event])))

(defonce renderer
  (p/create-renderer 500 500 {:view (.querySelector js/document "#canvas")
                              :background-color 0x65C25D}))

(defonce game
  (doto (p/create-game renderer)
    (p/start ["keydown"])
    (p/set-screens [main-screen])))

