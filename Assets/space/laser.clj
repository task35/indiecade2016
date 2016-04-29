(ns space.laser
  (:use arcadia.core
        arcadia.linear
        space.core)
  (:require [clojure.string :as string])
  (:import [UnityEngine
            GameObject
            Resources
            Sprite
            SpriteRenderer
            Input
            CircleCollider2D
            BoxCollider2D
            Rigidbody2D
            Component
            Time]))

(defn sprite [shape color]
  (Resources/Load
    (str "Space/PNG/Lasers/laser"
         (-> color name string/upper-case)
         (format "%02d" shape))
    Sprite))

;; lasers
(defn create [color position rotation speed]
  (let [sprite (sprite 4 color)
        obj (GameObject. (str (gensym "Laser")))
        rb (cmpt+ obj Rigidbody2D)]
    (set! (.. obj transform position)
          (v3 (.x position)
              (.y position)
              0))
    (set! (.layer obj) 13)
    (init! obj
           Rigidbody2D {gravityScale (float 0)
                        isKinematic true
                        position position
                        rotation rotation}
           SpriteRenderer {sprite sprite}
           BoxCollider2D {size (v2 (float 0.15)
                                   (float 0.37))
                          isTrigger true})
    (hook+ obj :on-trigger-enter2d
           (fn [go other]
             (destroy (.gameObject other) 0)
             (destroy go 0)))
    (hook+ obj :update
           (fn [go]
             (.MovePosition rb
               (v2+ (.position rb)
                    (v2* (heading go) (* speed Time/deltaTime)))))) ))