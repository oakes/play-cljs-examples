(ns super-koalio.core
  (:require [play-cljs.core :as p]
            [super-koalio.state :as s]
            [super-koalio.utils :as u]
            [nightlight.repl-server]))

;(set! *warn-on-infer* true)

(defonce game (p/create-game u/view-size u/view-size))
(defonce *state (atom {}))

(def main-screen
  (reify p/Screen
    (on-show [_]
      (p/load-image game u/image-url)
      (reset! *state (s/initial-state game)))
    (on-hide [_])
    (on-render [this]
      (let [{:keys [x y current]} @*state]
        (p/render game [[:stroke {}
                         [:fill {:color "lightblue"}
                          [:rect {:width u/view-size :height u/view-size}]]]
                        [:tiled-map {:name u/map-name :x x}]
                        [:div {:x u/koala-offset :y y :width u/koala-width :height u/koala-height}
                         current]])
        (when (> y (- (p/get-height game) u/koala-height))
          (p/set-screen game this)))
      (swap! *state
        (fn [state]
          (-> state
              (s/move game)
              (s/prevent-move game)
              (s/animate)))))))

(doto game
  (p/start)
  (p/set-screen main-screen))

