(ns space.demo
  (:use arcadia.core
        arcadia.linear
        common.core
        space.core)
  (:require [space.ship :as ship]
            [space.meteor :as meteor]
            [space.laser :as laser])
  (:import [UnityEngine Rigidbody2D])
  )

(comment
  
  ;; clean up
  (doseq [l (objects-named #".*\d+")]
    (destroy l))
  
  ;; basic game
  (ship/create 2 :red)
  (every 0.1 #'meteor/edge-create)
  
  (stop-all-coroutines)
  
  
  (.position (cmpt (object-tagged "Player") Rigidbody2D))
  (.velocity (cmpt (object-tagged "Player") Rigidbody2D))
  
  
  ;; single laser
  (laser/create :blue (v2 0) 0 1)
  
  (range 20)
  
  (range -20 20)
  
  (range -20 20 5)
  
  (doseq [a (range -20 20 5)]
    (log a))
  
  (doseq [a (range -20 20 5)]
    (laser/create :blue (v2 0) a 5))
  
  (let [base-angle 0
        cone 90
        density 15
        speed 5]
    (doseq [a (range (- cone) (+ cone 1) density)]
      (laser/create :blue (v2 0) (+ base-angle a) speed)))
  
  (defn spread-laser [color position base-angle cone density speed]
    (doseq [a (range (- cone) (+ cone 1) density)]
      (laser/create color position (+ base-angle a) speed))))