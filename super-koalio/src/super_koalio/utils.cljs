(ns super-koalio.utils
  (:require [play-cljs.core :as p]))

(def ^:const view-size 500)
(def ^:const duration 0.15)
(def ^:const damping 0.1)
(def ^:const max-velocity 0.5)
(def ^:const max-jump-velocity (* max-velocity 8))
(def ^:const deceleration 0.9)
(def ^:const gravity 0.3)

(defn decelerate
  [velocity]
  (let [velocity (* velocity deceleration)]
    (if (< (Math/abs velocity) damping)
      0
      velocity)))

(defn get-x-velocity
  [game {:keys [x-velocity]}]
  (cond
    (contains? (p/get-pressed-keys game) 37)
    (* -1 max-velocity)
    (contains? (p/get-pressed-keys game) 39)
    max-velocity
    :else
    x-velocity))

(defn get-y-velocity
  [game {:keys [y-velocity can-jump?]}]
  (cond
    (and can-jump? (contains? (p/get-pressed-keys game) 38))
    (* -1 max-jump-velocity)
    :else
    y-velocity))

(defn get-direction
  [{:keys [x-velocity direction]}]
  (cond
    (> x-velocity 0) :right
    (< x-velocity 0) :left
    :else
    direction))

