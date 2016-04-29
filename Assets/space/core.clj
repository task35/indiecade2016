(ns space.core
  (:use arcadia.core
        arcadia.linear)
  (:import IEnumerator
           UpdateHook
           [UnityEngine GameObject WaitForSeconds Time MonoBehaviour]))

(defn heading [go]
  (v2 (.. go transform up x)
      (.. go transform up y)))

(defn random-sprite [f & ranges]
  (apply f (map rand-nth ranges)))

(defmacro set-all! [x & forms]
  (let [pairs (partition 2 forms)
        sym (gensym "setall__")]
    `(let [~sym ~x]
       ~@(map (fn [[field value]]
                (list 'set! (list '. sym field) value))
              pairs)
       ~sym)))

(defmacro init! [obj & content]
  (let [sym (gensym "init__")
        hm (apply hash-map content)]
    `(let [~sym ~obj]
       ~@(map
           (fn [[t fields]]
             `(set-all! (ensure-cmpt ~sym ~t)
                        ~@(->> fields (apply concat))))
           hm))))

;; coro
(defn coro-root []
  (cmpt (or (object-named "coro-root")
            (let [go (GameObject. "coro-root")]
              (cmpt+ go UpdateHook) ;; hack 
              go))
        MonoBehaviour))

(defn every-frame [f]
  (.StartCoroutine (coro-root)
                   (reify IEnumerator
                     (MoveNext [this] (f))
                     (get_Current [this]))))

(defn every [n f]
  (.StartCoroutine (coro-root)
                   (reify IEnumerator
                     (MoveNext [this] (f) true)
                     (get_Current [this] (WaitForSeconds. n)))))

(defn in [n f]
  (let [wfs (WaitForSeconds. n)
        waiting? (volatile! false)]
    (.StartCoroutine (coro-root)
                     (reify IEnumerator
                       (MoveNext [this] (if @waiting?
                                          (do (f) false)
                                          true))
                       (get_Current [this]
                                    (vreset! waiting? true)
                                    wfs)))))

(defn stop-all-coroutines []
  (.StopAllCoroutines (coro-root)))