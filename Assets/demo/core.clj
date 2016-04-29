(ns demo.core
  (:use arcadia.core
        arcadia.linear
        common.core
        clojure.pprint)
  (:import Enemy Spawner
           [UnityEngine Mathf Collider2D Rigidbody2D]))

(comment
  (objects-tagged "Enemy")
  
  ;; set enemy move speeds
  (->> (objects-tagged "Enemy")
       (map #(set! (.. % transform localScale)
                   (v3 20)))
       (map #(cmpt % Enemy))
       (map #(set! (.moveSpeed %) 10))
       doall)
  
  ;; scale enemies
  (->> (objects-tagged "Enemy")
       (map #(set! (.. % transform localScale) (v3 1)))
       doall)
  
  ;; scale enemies sin
  (->> (objects-tagged "Enemy")
       (map-indexed
         (fn [i e]
           (set! (.. e transform localScale)
                 (v3 (* 10 (Math/Sin i))))))
       doall)
  
  ;; select enemies with hp > 1
  (->> (objects-typed Enemy)
       (filter #(> (.HP %) 1))
       (map gobj)
       into-array
       (set! Selection/objects))
  
  ;; destroy all enemies
  (->> (objects-tagged "Enemy")
       (map #(destroy %))
       doall)
  
  ;; clone platform
  (dotimes [_ 100]
    (-> "env_PlatformTop"
        object-named
        instantiate
        .name
        (set! "new-platform")))
  
  ;; position platforms
  (->> (objects-named "new-platform")
       (map-indexed
         (fn [i np]
           (let [; x (* 2 (if (odd? i) (- i) (+ i)))
                 x (* 10 (Mathf/Sin i))
                 y (+ 7 (* 15 i))]
             (set! (.. np transform position) (v3 x y 0))))))
  
  ;; collapse
  (->> (objects-typed Collider2D)
       (remove #(or (= (.. % gameObject name)
                       "env_PlatformBridge")
                    (= (.. % gameObject name)
                       "env_TowerFull")))
       (map gobj)
       (map #(cmpt+ % Rigidbody2D))
       doall)
  
    ;; destroy all platforms
  (->> (objects-named "new-platform")
       (map #(destroy %))
       doall)
  
  (load-level "shaders"))