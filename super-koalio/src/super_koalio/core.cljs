(ns super-koalio.core
  (:require [play-cljs.core :as p]
            [super-koalio.state :as s]
            [super-koalio.utils :as u]))

(declare game)

(def main-screen
  (reify p/Screen
    (on-show [_ state]
      (p/set-state game (merge state (s/initial-state game))))
    (on-hide [_ state])
    (on-render [_ {:keys [x y direction] :as state}]
      (let [x (if (= direction :left) (- x) x)]
        (p/render game [:fill {:color "lightblue"}
                        [:rect {:width u/view-size :height u/view-size}
                         [:div {:x x :y y :width s/tile-width :height s/tile-height}
                          (:current state)]]]))
      (p/set-state game
        (-> state
            (s/move game)
            (s/prevent-move game)
            (s/animate))))
    (on-event [_ state event])))

(defonce game (p/create-game u/view-size u/view-size))

(doto game
  (p/stop)
  (p/start ["keydown"])
  (p/set-screen main-screen))

