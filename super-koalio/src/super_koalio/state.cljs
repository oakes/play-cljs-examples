(ns super-koalio.state
  (:require [play-cljs.core :as p]
            [super-koalio.utils :as u]))

(def ^:const url "koalio.png")
(def ^:const tile-width 18)
(def ^:const tile-height 26)

(defn initial-state []
  (let [stand-right (p/sprite url {:frame {:x 0 :y 0 :width tile-width :height tile-height}})
        stand-left (p/sprite url {:frame {:x 0 :y 0 :width tile-width :height tile-height}
                                  :anchor [1 0]
                                  :scale [-1 1]})
        jump-right (p/sprite url {:frame {:x tile-width :y 0 :width tile-width :height tile-height}})
        jump-left (p/sprite url {:frame {:x tile-width :y 0 :width tile-width :height tile-height}
                                 :anchor [1 0]
                                 :scale [-1 1]})
        walk1-right (p/sprite url {:frame {:x (* 2 tile-width) :y 0 :width tile-width :height tile-height}})
        walk1-left (p/sprite url {:frame {:x (* 2 tile-width) :y 0 :width tile-width :height tile-height}
                                  :anchor [1 0]
                                  :scale [-1 1]})
        walk2-right (p/sprite url {:frame {:x (* 3 tile-width) :y 0 :width tile-width :height tile-height}})
        walk2-left (p/sprite url {:frame {:x (* 3 tile-width) :y 0 :width tile-width :height tile-height}
                                  :anchor [1 0]
                                  :scale [-1 1]})
        walk3-right (p/sprite url {:frame {:x (* 4 tile-width) :y 0 :width tile-width :height tile-height}})
        walk3-left (p/sprite url {:frame {:x (* 4 tile-width) :y 0 :width tile-width :height tile-height}
                                  :anchor [1 0]
                                  :scale [-1 1]})]
    {:current stand-right
     :stand-right stand-right
     :stand-left stand-left
     :jump-right jump-right
     :jump-left jump-left
     :walk-right (p/movie-clip [walk1-right walk2-right walk3-right] {:animation-speed 0.15})
     :walk-left (p/movie-clip [walk1-left walk2-left walk3-left] {:animation-speed 0.15})
     :x-velocity 0
     :y-velocity 0
     :x 100
     :y 0
     :can-jump? false
     :direction :right}))

(defn move
  [{:keys [x y can-jump?] :as state} game]
  (let [delta-time (p/get-delta-time game)
        x-velocity (u/get-x-velocity game state)
        y-velocity (+ (u/get-y-velocity game state) u/gravity)
        x-change (* x-velocity delta-time)
        y-change (* y-velocity delta-time)]
    (if (or (not= 0 x-change) (not= 0 y-change))
      (assoc state
             :x-velocity (u/decelerate x-velocity)
             :y-velocity (u/decelerate y-velocity)
             :x-change x-change
             :y-change y-change
             :x (+ x x-change)
             :y (+ y y-change)
             :can-jump? (if (neg? y-velocity) false can-jump?))
      state)))

(defn prevent-move
  [{:keys [x y x-change y-change] :as state} game]
  (let [max-y (- (p/get-height game) tile-height)
        old-y (- y y-change)
        up? (neg? y-change)]
    (merge state
           (when (> y max-y)
             {:y-velocity 0 :y-change 0 :y old-y :can-jump? (not up?)}))))

(defn animate
  [{:keys [x-velocity y-velocity
           stand-right stand-left
           jump-right jump-left
           walk-right walk-left] :as state}]
  (let [direction (u/get-direction state)]
    (-> state
        (assoc :current
          (cond
            (not= y-velocity 0)
            (if (= direction :right) jump-right jump-left)
            (not= x-velocity 0)
            (if (= direction :right) walk-right walk-left)
            :else
            (if (= direction :right) stand-right stand-left)))
        (assoc :direction direction))))

