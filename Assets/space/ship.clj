(ns space.ship
  (:use arcadia.core
        arcadia.linear
        common.core
        space.core)
  (:require [clojure.string :as string])
  (:require [space.laser :as laser])
  (:import [UnityEngine
            GameObject
            Resources
            Component
            Sprite
            SpriteRenderer
            Input
            CircleCollider2D
            BoxCollider2D
            Rigidbody2D
            Time]))

;; sprite loading
(defn player-sprite [shape color]
  (Resources/Load
    (str "Space/PNG/playerShip" shape "_" (name color)) Sprite))

(defn key? [k]
  (Input/GetKey (-> k name str)))

(defn key-down? [k]
  (Input/GetKeyDown (-> k name str)))

;; movement
(def forward-speed  500)
(def rotation-speed 150)

(defn forward [go]
  (cond
    (key? :up)
    (.AddForce (cmpt go Rigidbody2D)
               (v2* (heading go)
                    (* Time/deltaTime forward-speed)))
    
    (key? :down)
    (.AddForce (cmpt go Rigidbody2D)
               (v2* (heading go)
                    (* Time/deltaTime forward-speed -1)))))

(defn rotation [go]
  (cond
    (key? :left)
    (.MoveRotation (cmpt go Rigidbody2D)
                   (+ (.rotation (cmpt go Rigidbody2D))
                      (* Time/deltaTime rotation-speed)))
    
    (key? :right)
    (.MoveRotation (cmpt go Rigidbody2D)
                   (- (.rotation (cmpt go Rigidbody2D))
                      (* Time/deltaTime rotation-speed)))))


(defn fire [go]
  (cond
    (key-down? :space)
    (laser/create :green
                  (.position (cmpt go Rigidbody2D))
                  (.rotation (cmpt go Rigidbody2D))
                  10)))

(declare death)

;; player
(defn create [shape color]
  (let [player (GameObject. (str (gensym "player")))]
    (set! (.tag player) "Player")
    (set! (.layer player) 15)
    (hook+ player :update #'forward)
    (hook+ player :update #'rotation)
    (hook+ player :update #'fire)
    (hook+ player :on-collision-enter2d #'death)
    (init! player
           SpriteRenderer 
           {sprite (player-sprite shape color)}
           
           Rigidbody2D
           {drag 1
            angularDrag  1
            gravityScale 0}
           
           CircleCollider2D
           {radius (float 0.375)})))

(defn death [go collision]
  (when (= "meteor" (.. collision gameObject tag))
    (doseq [m (objects-tagged "meteor")]
      (destroy m 0))
    (destroy go 0)
    (in 1 #(create 2 :red))))