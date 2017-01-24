(ns flappy-bird-clone.core
  (:require [play-cljs.core :as p]
            [goog.events :as events]))

;(set! *warn-on-infer* true)

(defonce game (p/create-game 500 500))
(defonce state (atom {:timeoutid 0
                      :bird-p 100
                      :bird-v 0
                      :bird-a 1
                      :pipes []}))

(doto game
  (p/load-image "splash.png")
  (p/load-image "sky.png")
  (p/load-image "land.png")
  (p/load-image "Flappy_Bird.png")
  (p/load-image "pipe.png")
  (p/load-image "pipedwn.png"))

(declare title-screen)
(declare main-screen)

(defn calc-p [p v a]
  (let [t 0.25]
    (+ (* 0.5 t t a) (* t v) p)))

(defn calc-v [v a]
  (let [t 0.25]
    (+ (* t a) v)))

(defn move-bird [{:keys [bird-p bird-v bird-a] :as state}]
  (assoc state
    :bird-p (calc-p bird-p bird-v bird-a)
    :bird-v (calc-v bird-v bird-a)))

;If we are on the title screen a mouse click takes us to the next screen,
;otherwise we minus the bird's y position to make it jump.
(events/listen js/window "mousedown"
               (fn [_]
                 (let [gme (p/get-screen game)]
                   (cond
                     (= gme title-screen) (p/set-screen game main-screen)
                     (= gme main-screen) (swap! state update-in [:bird-v] #(- 12))))))

;Top and bottom pipes are generated together as the gap between them should
;always be the same.
(defn pipe-gen []
  (let [rnd (rand 350)]
    [[:image {:name "pipedwn.png" :width 50 :height 400 :x 550 :y (+ -400 rnd)}]
     [:image {:name "pipe.png" :width 50 :height 400 :x 550 :y (+ 200 rnd)}]]))

;For two rectangles, if we know the top left and bottom right co-ordinates,
;there are four cases which if true mean those two rectangles are not
;overlapping, otherwise they must be. Details here:
;http://stackoverflow.com/questions/23302698/java-check-if-two-rectangles-overlap-at-any-point
(defn collision-detection [images [_ {:keys [x y width height] :as bird}]]
  (let [diags (map
                (fn [[_ {:keys [x y width height] :as image}]]
                  {:x1 x :y1 y :x2 (+ x width) :y2 (+ y height)})
                images)
        overlap-check (fn [{:keys [x1 y1 x2 y2]}]
                        (let [birdx1 x birdy1 y birdx2 (+ x 60) birdy2 (+ y 60)]
                          (cond
                            (< birdx2 x1) false
                            (> birdx1 x2) false
                            (> birdy1 y2) false
                            (> y1 birdy2) false
                            :overlapping true)))
        overlaps (map overlap-check diags)]
    (some #(= true %) overlaps)))

(def main-screen
  (reify p/Screen

    (on-show [this]
      ;Every four seconds we add two new pipes to a filtered list of the old pipes,
      ;where we remove pipes that have gone off the screen to the left.
      ;We also need to record the id of our call to setInterval so we can
      ;destroy it when we leave this screen.
      (swap! state update-in [:timeoutid]
             (fn [_] (js/setInterval
                       (fn []
                         (swap! state update-in [:pipes]
                                (fn [pipes]
                                  (apply conj (filter
                                                (fn [pipe]
                                                  (< 0 (get-in pipe [1 :x]))) pipes)
                                         (pipe-gen)))))
                       4000))))

    (on-hide [this]
      (js/clearInterval (:timeoutid @state)))

    (on-render [this]
      (let [{:keys [bird-p pipe pipes]} @state
            bird-img [:image {:name "Flappy_Bird.png" :width 60 :height 60 :x 200 :y bird-p}]]

        ;If the bird hits the ground or a pipe, return to the title screen and
        ;reset its position.
        (when (or (< 400 bird-p) (collision-detection pipes bird-img))
          (do
            (swap! state update-in [:pipes] (fn [_] []))
            (swap! state update-in [:bird-p] (fn [_] 0))
            (p/set-screen game title-screen)))

        ; Make the bird fall!
        (swap! state move-bird)

        ; Move all of our pipes to the left, to the left.
        (swap! state update-in [:pipes] (fn [pipes] (map
                                                      (fn [pipe]
                                                        (update-in pipe [1 :x] dec))
                                                      pipes)))

        (p/render game
                  [[:image {:name "sky.png" :width 500 :height 500 :x 0 :y 0}]
                   [:image {:name "land.png" :width 500 :height 100 :x 0 :y 450}]
                   bird-img])

        (p/render game pipes)))))

(def title-screen
  (reify p/Screen
    (on-show [this])
    (on-hide [this])
    (on-render [this]
      (p/render game
                [[:image {:name "sky.png" :width 500 :height 500 :x 0 :y 0}]
                 [:image {:name "splash.png" :width 300 :height 300 :x 100 :y 100}]
                 [:image {:name "land.png" :width 500 :height 100 :x 0 :y 450}]]))))

(doto game
  (p/start)
  (p/set-screen title-screen))
