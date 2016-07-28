(ns super-koalio.core
  (:require [play-cljs.core :as p]))

(def main-screen
  (reify p/Screen
    (on-show [_ state])
    (on-hide [_ state])
    (on-render [_ state timestamp]
      (p/sprite "koalio.png"))
    (on-event [_ state event])))

(defonce renderer
  (doto (p/create-renderer 500 500 {:background-color 0x65C25D})
    (->> .-view (.appendChild js/document.body))))

(defonce game
  (doto (p/create-game renderer {:koalio {:x 0 :y 0}})
    (p/start ["keydown"])
    (p/set-screens [main-screen])))

