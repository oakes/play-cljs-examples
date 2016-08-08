(ns super-koalio.state
  (:require [play-cljs.core :as p]
            [super-koalio.utils :as u]))

(def ^:const url "koalio.png")
(def ^:const tile-width 18)
(def ^:const tile-height 26)

(defn initial-state [game]
  (let [image (p/load-image game "koalio.png")
        stand-right [:img {:object image :swidth tile-width :sheight tile-height}]
        stand-left [:img {:object image :swidth tile-width :sheight tile-height :scale-x -1 :width (- tile-width)}]]
    {:koalio (p/load-image game "koalio.png")
     :current stand-right
     :stand-right stand-right
     :stand-left stand-left
     ;:jump-right TODO
     ;:jump-left TODO
     ;:walk-right TODO
     ;:walk-left TODO
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
            ;(not= y-velocity 0)
            ;(if (= direction :right) jump-right jump-left)
            ;(not= x-velocity 0)
            ;(if (= direction :right) walk-right walk-left)
            :else
            (if (= direction :right) stand-right stand-left)))
        (assoc :direction direction))))

