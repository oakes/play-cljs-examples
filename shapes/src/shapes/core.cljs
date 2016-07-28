(ns shapes.core
  (:require [play-cljs.core :as p]))

(declare renderer)

(def main-screen
  (reify p/Screen
    (on-show [_ state])
    (on-hide [_ state])
    (on-render [_ state timestamp]
      (p/graphics
        [:fill {:color 0xe74c3c :alpha 1}
         [:polygon {:path [0 0, 0 50, 50 50, 50 0]}
          [:fill {:color 0x9b59b6 :alpha 1}
           [:rect {:x 10 :y 10 :width 20 :height 20}]
           [:circle {:x 20 :y 40 :radius 10}]]]]
        (get-in state [:shape :x])
        (get-in state [:shape :y])))
    (on-event [_ state event]
      (when (= (.-type event) "mousemove")
        (-> state
            (assoc-in [:shape :x] (- (.-clientX event) (-> renderer .-view .-offsetLeft)))
            (assoc-in [:shape :y] (- (.-clientY event) (-> renderer .-view .-offsetTop)))
            p/reset-state)))))

(defonce renderer
  (doto (p/create-renderer 500 500 {:background-color 0x65C25D})
    (->> .-view (.appendChild js/document.body))))

(defonce game
  (doto (p/create-game renderer {:shape {:x 0 :y 0}})
    (p/start ["mousemove"])
    (p/set-screens [main-screen])))

