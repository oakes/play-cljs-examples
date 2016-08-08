(ns shapes.core
  (:require [play-cljs.core :as p]))

(declare game)

(def main-screen
  (reify p/Screen
    (on-show [_ state]
      (p/set-state game
        (assoc state
          :text ["Hello, world!" {:x 0 :y 50 :size 16 :font "Georgia" :style :italic}
                 ["Hi there" {:y 50 :size 32 :font "Helvetica" :style :normal}]])))
    (on-hide [_ state])
    (on-render [_ state]
      (p/render game (:text state)))
    (on-event [_ state event])))

(def overlay-screen
  (reify p/Screen
    (on-show [_ state])
    (on-hide [_ state])
    (on-render [_ state]
      (p/render game [:ellipse {:x (:shapes-x state) :y (:shapes-y state) :width 60 :height 60}
                      [:arc {:width 50 :height 50 :start 0 :stop 3.14}]
                      ;[:quad {:x1 -10 :y1 -15 :x2 10 :y2 -15 :x3 10 :y3 15 :x4 -10 :y4 15}]
                      [:rect {:x -10 :y -15 :width 20 :height 30}]
                      [:line {:x1 -10 :y1 -5 :x2 10 :y2 -5}]
                      [:triangle {:x1 -10 :y1 -15 :x2 10 :y2 -15 :x3 0 :y3 15}]]))
    (on-event [_ state event]
      (when (= (.-type event) "mousemove")
        (let [canvas (p/get-canvas game)
              x-offset (max 0 (- (.-clientWidth canvas) (.-clientHeight canvas)))
              y-offset (max 0 (- (.-clientHeight canvas) (.-clientWidth canvas)))
              x-adjust (/ (p/get-width game) (- (.-clientWidth canvas) x-offset))
              y-adjust (/ (p/get-height game) (- (.-clientHeight canvas) y-offset))
              new-x (* x-adjust (- (.-clientX event) (/ x-offset 2)))
              new-y (* y-adjust (- (.-clientY event) (/ y-offset 2)))]
          (p/set-state game
            (assoc state :shapes-x new-x :shapes-y new-y)))))))

(defonce game (p/create-game 500 500))

(doto game
  (p/stop)
  (p/start ["mousemove"])
  (p/set-screens [main-screen overlay-screen]))

