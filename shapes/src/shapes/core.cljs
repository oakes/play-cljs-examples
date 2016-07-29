(ns shapes.core
  (:require [play-cljs.core :as p]))

(declare renderer)

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
    (on-render [_ state total-time delta-time]
      (:shapes state))
    (on-event [_ state event]
      (when (= (.-type event) "mousemove")
        (-> state
            (assoc-in [:shapes :x] (- (.-clientX event) (-> renderer .-view .-offsetLeft)))
            (assoc-in [:shapes :y] (- (.-clientY event) (-> renderer .-view .-offsetTop)))
            p/reset-state)))))

(defonce renderer
  (p/create-renderer 500 500 {:view (.querySelector js/document "#canvas")
                              :background-color 0x65C25D}))

(defonce game
  (doto (p/create-game renderer)
    (p/start ["mousemove"])
    (p/set-screens [main-screen])))

