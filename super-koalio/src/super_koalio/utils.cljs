(ns super-koalio.utils
  (:require [play-cljs.core :as p]))

(def ^:const duration 0.15)
(def ^:const damping 0.5)
(def ^:const max-velocity 0.5)
(def ^:const max-jump-velocity (* max-velocity 4))
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
    (p/key-pressed? game :arrow-left)
    (* -1 max-velocity)
    (p/key-pressed? game :arrow-right)
    max-velocity
    :else
    x-velocity))

(defn get-y-velocity
  [game {:keys [y-velocity can-jump?]}]
  (cond
    (and can-jump? (p/key-pressed? game :arrow-up))
    (* -1 max-jump-velocity)
    :else
    y-velocity))

