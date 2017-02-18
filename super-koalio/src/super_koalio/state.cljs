(ns super-koalio.state
  (:require [play-cljs.core :as p]
            [super-koalio.utils :as u]))

;(set! *warn-on-infer* true)

(defn initial-state [game]
  (let [stand-right [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx 0}]
        stand-left [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx 0 :flip-x true}]
        jump-right [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx u/koala-width}]
        jump-left [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx u/koala-width :flip-x true}]
        walk-right-1 [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx (* 2 u/koala-width)}]
        walk-right-2 [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx (* 3 u/koala-width)}]
        walk-right-3 [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx (* 4 u/koala-width)}]
        walk-left-1 [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx (* 2 u/koala-width) :flip-x true}]
        walk-left-2 [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx (* 3 u/koala-width) :flip-x true}]
        walk-left-3 [:image {:name u/image-url :swidth u/koala-width :sheight u/koala-height :sx (* 4 u/koala-width) :flip-x true}]]
    {:current stand-right
     :stand-right stand-right
     :stand-left stand-left
     :jump-right jump-right
     :jump-left jump-left
     :walk-right [:animation {:duration 200} walk-right-1 walk-right-2 walk-right-3]
     :walk-left [:animation {:duration 200} walk-left-1 walk-left-2 walk-left-3]
     :x-velocity 0
     :y-velocity 0
     :x -100
     :y 200
     :can-jump? false
     :direction :right
     :map (p/load-tiled-map game u/map-name)}))

(defn move
  [{:keys [x y can-jump?] :as state} game]
  (let [delta-time (min (p/get-delta-time game) 100)
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
  (let [old-x (- x x-change)
        old-y (- y y-change)
        up? (neg? y-change)]
    (merge state
      (when (u/touching-tile? (:map state) 1 (+ x u/koala-offset) old-y u/koala-width u/koala-height)
        {:x-velocity 0 :x-change 0 :x old-x})
      (when (u/touching-tile? (:map state) 1 (+ old-x u/koala-offset) y u/koala-width u/koala-height)
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

