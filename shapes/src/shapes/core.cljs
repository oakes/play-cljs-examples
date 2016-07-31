(ns shapes.core
  (:require [play-cljs.core :as p]))

(declare canvas renderer game)

(def ^:const view-size 500)

(def main-screen
  (reify p/Screen
    (on-show [_ state]
      (p/reset-state
        (assoc state
          :background (p/graphics
                        [:fill {:color 0x65C25D :alpha 1}
                         [:rect {:x 0 :y 0 :width view-size :height view-size}]])
          :label (p/text "Hello, world!" {:fill 0xFFFFFF :x 100 :y 100}))))
    (on-hide [_ state])
    (on-render [_ state]
      [(:background state)
       (:label state)])
    (on-event [_ state event])))

(def overlay-screen
  (reify p/Screen
    (on-show [_ state]
      (p/reset-state
        (assoc state
          :shapes (p/graphics
                    [:fill {:color 0xe74c3c :alpha 1}
                     [:polygon {:path [0 0, 0 50, 50 50, 50 0]}
                      [:fill {:color 0x9b59b6 :alpha 1}
                       [:rect {:x 10 :y 10 :width 20 :height 20}]
                       [:circle {:x 20 :y 40 :radius 10}]]]]))))
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
  (p/create-renderer view-size view-size {:view canvas}))

(defonce game (p/create-game renderer))

(doto game
  (p/stop)
  (p/start ["mousemove"])
  (p/set-screens [main-screen overlay-screen]))

