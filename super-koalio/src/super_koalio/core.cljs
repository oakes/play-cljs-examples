(ns super-koalio.core
  (:require [play-cljs.core :as p]
            [super-koalio.state :as s]
            [super-koalio.utils :as u]))

(defonce game (p/create-game u/view-size u/view-size))
(defonce state (atom {}))

(def main-screen
  (reify p/Screen
    (on-show [_]
      (reset! state (s/initial-state game)))
    (on-hide [_])
    (on-render [_]
      (let [{:keys [x y koala-x direction current]} @state
            koala-x (if (= direction :left) (- koala-x) koala-x)]
        (p/render game [[:stroke {}
                         [:fill {:color "lightblue"}
                          [:rect {:width u/view-size :height u/view-size}]]]
                        [:tiled-map {:object (:map @state) :x x}]
                        [:div {:x koala-x :y y :width u/tile-width :height u/tile-height}
                         current]]))
      (reset! state
        (-> @state
            (s/move game)
            (s/prevent-move game)
            (s/animate))))
    (on-event [_ event])))

(doto game
  (p/stop)
  (p/start ["keydown"])
  (p/set-screen main-screen))

