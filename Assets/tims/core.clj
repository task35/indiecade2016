(ns tims.core
  (:refer-clojure :exclude [aget])
  (:use seascape.core
        arcadia.core
        arcadia.linear
        common.core
        gamma-tools.core
        clojure.pprint)
  (:require [gamma.api :as g]
            ;;       [arcadia.updater :as updr]
            [gamma.program :as p]
            [arcadia.internal.map-utils :as mu]))


;; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
;; ============================================================
;; SWAP TO ENABLE GAMMA EMISSION
(def ^:dynamic *emit-shaders*
  ;;false
  true
  )
;; ============================================================
;; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

(require '[arcadia.introspection :as intro])

(defn mempr [& args]
  (pprint (apply intro/members args)))

(defmacro clobs [[name body] & bodies]
  `(as-> ~body ~name ~@bodies))

(defn sin-phase [x phase]
  (g/sin (gdiv (g* x 2 Mathf/PI) phase)))

(defn numdist [a b]
  (g/abs (g- a b)))

(defn modnrm [a b]
  (gdiv (g/mod a b) b))

(def pi Mathf/PI)

(def two-pi
  (float (* Mathf/PI 2)))
  
  ;; bit o sugar

(defmacro defshader
  ([name body]
   `(def ~name ~body))
  ([name shader-name shader-dir body]
   `(let [shader# ~body]
      (when *emit-shaders*
        (write-shader ~shader-name ~shader-dir shader#))
      (def ~name shader#))))

(defshader sphere-practice "geronimo" shader-dir
  (let [wobble (g/uniform "wobble" :vec4)
        object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [dist (g/distance position-in-world-space, (g/vec4 0 0 0 1))]
        (g/if (g/< dist 5)
          (g/vec4 0 1 0 1)
          (g/vec4 0.3 0.3 0.3 1)))}}))

(defshader spiral-practice "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [dist (g/distance position-in-world-space, (g/vec4 0 0 0 1))]
        (g/if (g/< dist 5)
          (g/vec4 0 1 0 1)
          (g/vec4 0.3 0.3 0.3 1)))}}))

(defshader concentro "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [dist (g/distance position-in-world-space, (g/vec4 0 0 0 1))]
        (g/* (g/vec4 1) (gdiv (g/mod dist 1) 5)))}}))

(defshader concentro-2 "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [dist (g/distance position-in-world-space, (g/vec4 0 0 0 1))
            phase 1
            phase-dist (g/mod dist phase)
            val (g/abs (g/- phase-dist (g/div phase 2)))]
        (g/* (g/vec4 1) val))}}))

(defshader concentro-2 "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:transparent true
     :vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [phase 0.1
            dist (clobs [dist (g/distance
                                position-in-world-space,
                                (g/vec4 0 0 0 1))]
                   (g/abs (g- (g/mod dist, phase)
                            (gdiv phase 2)))
                   (g/div dist
                     (gdiv phase 2)))
            val dist]
        (g/vec4 0 0 0 val))}}))

(defshader concentro-3 "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:transparent true
     :vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [phase 0.1
            val (g+ Mathf/PI
                  (g/atan
                    (gx position-in-world-space)
                    (gy position-in-world-space)))
            scaled-val (gdiv val (g* Mathf/PI 2))]
        (g/vec4 0 0 0 scaled-val))}}))

(defshader radial-1 "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:transparent true
     :vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [phase (float (/ 1 100))
            angnrm (clobs [ang (g/atan
                                 (gx position-in-world-space)
                                 (gy position-in-world-space))]
                     (g+ ang Mathf/PI)
                     (gdiv ang (g* Mathf/PI 2)))
            val (clobs [v (g/mod angnrm phase)]
                  (gdiv v phase)
                  (g/smoothstep 0 0.5
                    (g/abs (g- v 0.5))))]
        (g/vec4 0 0 0 val))}}))


(defshader radial-2 "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:transparent true
     :vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [ang-phase (float (/ 1 10))
            rad-phase (float (/ 1 10))
            angnrm (clobs [ang (g/atan
                                 (gx position-in-world-space)
                                 (gy position-in-world-space))]
                     (g+ ang Mathf/PI)
                     (gdiv ang (g* Mathf/PI 2)))
            dist (g/distance position-in-world-space, (g/vec4 0 0 0 1))
            val (clobs [v (g/mod
                            (g+ angnrm (sin-phase dist rad-phase))
                            ang-phase)]
                  (gdiv v ang-phase)
                  (g/smoothstep 0 0.5
                    (g/abs (g- v 0.5))))]
        (g/vec4 0 0 0 val))}}))

(defshader radial-3 "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:transparent true
     :vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [ang-phase (float (/ 1 10))
            rad-phase (float (/ 1 10))
            angnrm (clobs [ang (g/atan
                                 (gx position-in-world-space)
                                 (gy position-in-world-space))]
                     (g+ ang Mathf/PI)
                     (gdiv ang (g* Mathf/PI 2)))
            dist (g/distance position-in-world-space, (g/vec4 0 0 0 1))
            val-1 (clobs [v (g/mod
                              (g+ angnrm (sin-phase dist rad-phase))
                              ang-phase)]
                    (gdiv v ang-phase)
                    (g/smoothstep 0 0.5
                      (g/abs (g- v 0.5))))
            val-2 (let [subang (gdiv (g/mod angnrm ang-phase) ang-phase)
                        subsweep-length (g* (g* 2 Mathf/PI dist) rad-phase)
                        subang-2 (g/mod
                                   (g+ subang
                                     (gdiv
                                       (g/sin (sin-phase dist 1))
                                       subsweep-length))
                                   1)
                        mark-len 0.3
                        mark-len-nrm (gdiv mark-len subsweep-length)]
                    (clobs [v subang-2]
                      (g/smoothstep
                        (g- 1 mark-len-nrm)
                        1 v)
                      ;;(g+ val-1 v)
                      ))]
        (g/vec4 0 0 0 val-2))}}))


(defshader radial-4 "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:transparent true
     :vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [ang-phase (float (/ 1 20))
            rad-phase (float (/ 1 10))
            mark-len 0.5
            distortion-magnitude 1
            angnrm (clobs [ang (g/atan
                                 (gx position-in-world-space)
                                 (gy position-in-world-space))]
                     (g+ ang Mathf/PI)
                     (gdiv ang (g* Mathf/PI 2)))
            dist (g/distance position-in-world-space, (g/vec4 0 0 0 1))
            val-1 (clobs [v (g/mod
                              (g+ angnrm (sin-phase dist rad-phase))
                              ang-phase)]
                    (gdiv v ang-phase)
                    (g/smoothstep 0 0.5
                      (g/abs (g- v 0.5))))
            subang (gdiv (g/mod angnrm ang-phase) ang-phase)
            subsweep-length (g* (g* 2 Mathf/PI dist)
                              ang-phase)
            subang-2 (clobs [sa
                             #_(noise (g/vec2
                                       (gx position-in-world-space)
                                       (gy position-in-world-space)))
                             (sin-phase dist 1)]
                       (gdiv sa subsweep-length)
                       (g* sa distortion-magnitude)
                       (g+ subang sa)
                       (g/mod sa 1))
            mark-len-nrm (gdiv mark-len subsweep-length)
            val-2 (clobs [v subang-2]
                    (g* (numdist v 0.5) 2)
                    (g/smoothstep
                      (g- 1 mark-len-nrm)
                      1 v)
                    ;;(g+ val-1 v)
                    )]
        (g/vec4 0 0 0 val-2))}}))


(defshader radial-5 "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:transparent true
     :vertex-shader
     {position-in-world-space (g* object->world glv)
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [ang-phase (float (/ 1 120))
            rad-phase 13
            mark-len 1
            distortion-magnitude 2
            angnrm (-> (g/atan
                         (gx position-in-world-space)
                         (gy position-in-world-space))
                     (g+ Mathf/PI)
                     (gdiv (g* Mathf/PI 2)))
            dist (g/distance position-in-world-space, (g/vec4 0 0 0 1))
            val-1  (modnrm dist rad-phase)
            subang (modnrm angnrm ang-phase)
            subsweep-length (g* (g* 2 Mathf/PI dist)
                              ang-phase)
            subang-2 (-> (noise
                           (g* 0.9
                             (g/vec2
                               (gx position-in-world-space)
                               (gy position-in-world-space))))
                       (gdiv subsweep-length)
                       (g* distortion-magnitude)
                       (g+ subang)
                       (g/mod 1))
            mark-len-nrm (gdiv mark-len subsweep-length)
            val-2 (clobs [v subang-2]
                    (g* (numdist v 0.5) 2)
                    (g/smoothstep
                      (g- 1 mark-len-nrm)
                      1 v)
                    (g- val-1 v))]
        (g/vec4 0 0 0 val-2))}}))

(defshader radial-6 "geronimo" shader-dir
  (let [object->world (not-prop (g/uniform "_Object2World" :mat4))
        time (not-prop (g/uniform "_Time" :vec4))
        vtime (g/varying "vtime" :vec4)
        sphere-center (g/uniform "sphere_center" :vec4)
        position-in-world-space (g/varying "position_in_world_space" :vec4)]
    {:transparent true
     :vertex-shader
     {position-in-world-space (g* object->world glv)
      ;; (g- (g* object->world glv)
                              ;;   sphere-center)
      vtime time
      (g/gl-position) (g* glmvpm glv)}
     
     :fragment-shader
     {(g/gl-frag-color)
      (let [ang-phase (float (/ 1 120))
            rad-phase 13
            t (g/aget vtime (g/int 1))
            mark-len 1
            distortion-magnitude 2
            angnrm (-> (g/atan
                         (gx position-in-world-space)
                         (gy position-in-world-space))
                     (g+ Mathf/PI)
                     (gdiv (g* Mathf/PI 2)))
            dist (g/distance position-in-world-space, (g/vec4 0 0 0 1))
            val-1  (modnrm dist rad-phase)
            subang (modnrm angnrm ang-phase)
            subsweep-length (g* (g* 2 Mathf/PI dist)
                              ang-phase)
            subang-2 (-> (noise
                           (g* 0.9
                             (g/vec2
                               (gx position-in-world-space)
                               (gy position-in-world-space))))
                       (gdiv subsweep-length)
                       (g* distortion-magnitude)
                       (g+ subang)
                       (g/mod 1))
            mark-len-nrm (gdiv mark-len subsweep-length)
            val-2 (clobs [v subang-2]
                    (g* (numdist v 0.5) 2)
                    (g/smoothstep
                      (g- 1 mark-len-nrm)
                      1 v)
                    (g- val-1 v))]
        (g/vec4 0 0 0
          (g* (gdiv t 100)
           val-2)))}}))

;; demo fun
(comment
  (use 'arcadia.core)
  
  ;; clone cube
  (set! (.name (instantiate (object-named "Cube")))
        "Cube 2")
  
  ;; rotate both
  (do
    ;; rotate cube
    (hook+ (object-named "Cube")
           :update
           #(.. % transform (Rotate 1 0 0)))
    
    ;; rotate cube 2
    (hook+ (object-named "Cube 2")
           :update
           #(.. % transform (Rotate 0 1 0))))
  
  ;; reset rotations
  (doseq [o (objects-named #"Cube.*")]
    (set! (.. o transform rotation) Quaternion/identity))
  
  ;; next scene
  (load-level "space"))