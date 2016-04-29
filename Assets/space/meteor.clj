(ns space.meteor
  (:use space.core
        arcadia.core
        arcadia.linear)
  (:require [clojure.string :as string])
  (:import [UnityEngine Rigidbody2D Time Sprite Transform CircleCollider2D Component SpriteRenderer Resources]))

(defn sprite
  ([] (random-sprite sprite (range 1 3) '[brown grey] '[big med small tiny]))
  ([size] (random-sprite sprite (range 1 3) '[brown grey] [size]))
  ([shape color size]
   (Resources/Load
     (str "Space/PNG/Meteors/meteor"
          (-> color name string/upper-case) "_"
          (name size)
          shape) Sprite)))

(defn slow-rotate [s]
  (fn [go] (.. go transform (Rotate 0 0 (* s Time/deltaTime)))))

(defn drift-towards [source destination]
  (let [v (-> (v3- destination source)
              .normalized
              (v3* Time/deltaTime))]
    (fn [go]
      (.. go transform
          (Translate v Space/World)))))

(defn create [position]
  (let [obj (GameObject. (str (gensym "meteor")))]
    (set! (.tag obj) "meteor")
    (set! (.layer obj) 14)
    (hook+ obj :update
           (slow-rotate (- (rand 100) 50)))
    (hook+ obj :update
           (drift-towards position (v3 0)))
    (init! obj
           Transform {position position}
           SpriteRenderer {sprite (sprite)}
           CircleCollider2D {isTrigger false}
           Rigidbody2D {gravityScale 0
                        isKinematic true})))

(defn edge-create []
  (let [r (UnityEngine.Random/insideUnitCircle)
        v (.normalized (v3 (.x r) (.y r) 0))]
    (create (v3+ (v3 0 0 0)
                 (v3* v (+ 4 (rand)))))))