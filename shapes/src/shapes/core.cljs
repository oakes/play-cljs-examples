(ns shapes.core
  (:require [play-cljs.core :as p]))

(declare canvas renderer game)

(def main-screen
  (reify p/Screen
    (on-show [_ state]
      (p/reset-state
        {:shapes (p/graphics
                   [:fill {:color 0xe74c3c :alpha 1}
                    [:polygon {:path [0 0, 0 50, 50 50, 50 0]}
                     [:fill {:color 0x9b59b6 :alpha 1}
                      [:rect {:x 10 :y 10 :width 20 :height 20}]
                      [:circle {:x 20 :y 40 :radius 10}]]]])}))
    (on-hide [_ state])
    (on-render [_ state]
      (:shapes state))
    (on-event [_ state event]
      (when (= (.-type event) "mousemove")
        (let [x-offset (max 0 (- (.-clientWidth canvas) (.-clientHeight canvas)))
              y-offset (max 0 (- (.-clientHeight canvas) (.-clientWidth canvas)))
              x-adjust (/ (p/get-width game) (- (.-clientWidth canvas) x-offset))
              y-adjust (/ (p/get-height game) (- (.-clientHeight canvas) y-offset))
              new-x (* x-adjust (- (.-clientX event) (/ x-offset 2)))
              new-y (* y-adjust (- (.-clientY event) (/ y-offset 2)))]
          (-> state
              (assoc-in [:shapes :x] new-x)
              (assoc-in [:shapes :y] new-y)
              p/reset-state))))))

(def canvas (.querySelector js/document "#canvas"))

(defonce renderer
  (p/create-renderer 500 500 {:view canvas
                              :background-color 0x65C25D}))

(defonce game (p/create-game renderer))

(doto game
  (p/stop)
  (p/start ["mousemove"])
  (p/set-screens [main-screen]))

