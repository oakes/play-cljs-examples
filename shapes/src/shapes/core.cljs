(ns shapes.core
  (:require [play-cljs.core :as p]
            [goog.events :as events]))

;(set! *warn-on-infer* true)

(defonce game (p/create-game (.-innerWidth js/window) (.-innerHeight js/window)))
(defonce *state (atom {:shapes-x 0 :shapes-y 0}))

(def rgb-content
  [:stroke {}
   [:rgb {:max-r 100 :max-g 100 :max-b 100 :max-a 100}
    (for [i (range 100)
          j (range 100)]
      [:stroke {:colors [i j 0]}
       [:point {:x i :y j}]])]])

(def hsb-content
  [:stroke {}
   [:hsb {:max-h 100 :max-s 100 :max-b 100 :max-a 100}
    (for [i (range 100)
          j (range 100)]
      [:stroke {:colors [i j 100]}
       [:point {:x i :y j}]])]])

(def main-screen
  (reify p/Screen
    (on-show [_]
      ; pre-render images so we don't need to render them every single frame
      (p/pre-render game "rgb-image" 100 100 rgb-content)
      (p/pre-render game "hsb-image" 100 100 hsb-content))
    (on-hide [_])
    (on-render [_]
      (p/render game
        [[:text {:value "Hello, world!" :x 0 :y 50 :size 16 :font "Georgia" :style :italic}
          [:text {:value "Hi there" :y 50 :size 32 :font "Helvetica" :style :normal}]]
         [:div {:x 10 :y 100}
          [:fill {}
           [:stroke {:colors [255 102 0]}
            [:line {:x1 85 :y1 20 :x2 10 :y2 10}]
            [:line {:x1 90 :y1 90 :x2 15 :y2 80}]]
           [:stroke {:colors [0 0 0]}
            [:bezier {:x1 85 :y1 20 :x2 10 :y2 10 :x3 90 :y3 90 :x4 15 :y4 80}]]]]
         [:div {:x 100 :y 100}
          [:fill {}
           [:stroke {:colors [255 102 0]}
            [:curve {:x1 5 :y1 26 :x2 5 :y2 26 :x3 73 :y3 24 :x4 73 :y4 61}]]
           [:stroke {:grayscale 0}
            [:curve {:x1 5 :y1 26 :x2 73 :y2 24 :x3 73 :y3 61 :x4 15 :y4 65}]]
           [:stroke {:colors [255 102 0]}
            [:curve {:x1 73 :y1 24 :x2 73 :y2 61 :x3 15 :y3 65 :x4 15 :y4 65}]]]]
         [:image {:name "rgb-image" :x 200 :y 100}]
         [:image {:name "hsb-image" :x 300 :y 100}]
         [:smiley {:x 100 :y 300}]
         [:ellipse {:x (:shapes-x @*state) :y (:shapes-y @*state) :width 60 :height 60}
          [:arc {:width 50 :height 50 :start 0 :stop 3.14}]
          ;[:quad {:x1 -10 :y1 -15 :x2 10 :y2 -15 :x3 10 :y3 15 :x4 -10 :y4 15}]
          [:rect {:x -10 :y -15 :width 20 :height 30}]
          [:line {:x1 -10 :y1 -5 :x2 10 :y2 -5}]
          [:triangle {:x1 -10 :y1 -15 :x2 10 :y2 -15 :x3 0 :y3 15}]]]))))

; example of how to define a new entity type
(defmethod p/draw-sketch! :smiley [game ^js/p5 renderer content parent-opts]
  (let [[_ opts & children] content
        opts (play-cljs.options/update-opts opts parent-opts play-cljs.options/basic-defaults)]
    (p/draw-sketch!
      game
      renderer
      [:fill {:color "yellow"}
       [:ellipse {:width 100 :height 100}
        [:fill {:color "black"}
         [:ellipse {:x -20 :y -10 :width 10 :height 10}]
         [:ellipse {:x 20 :y -10 :width 10 :height 10}]]
        [:fill {}
         [:arc {:width 60 :height 60 :start 0 :stop 3.14}]]]]
      opts)
    (p/draw-sketch! game renderer children opts)))

(events/listen js/window "mousemove"
  (fn [event]
    (swap! *state assoc :shapes-x (.-clientX event) :shapes-y (.-clientY event))))

(events/listen js/window "resize"
  (fn [event]
    (p/set-size game (.-innerWidth js/window) (.-innerHeight js/window))))

(doto game
  (p/start)
  (p/set-screen main-screen))

