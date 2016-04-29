(ns common.core
  (:use arcadia.core)
  (:import [UnityEngine Application]
           [UnityEditor EditorApplication]))

(defn load-level [name]
  (Application/LoadLevel name))

(defn reload-level [name]
  (Application/LoadLevel Application/loadedLevel))

(defn toggle-pause []
  (set! EditorApplication/isPaused (not EditorApplication/isPaused)))

(defn pause []
  (set! EditorApplication/isPaused true))

(defn unpause []
  (set! EditorApplication/isPaused false))

(defmacro chance [& body]
  (let [r (gensym "chance")
        pairs (sort-by first (partition 2 body))
        odds (map first pairs)
        exprs (map last pairs)
        sum (apply + odds)
        fracs (map #(float (/ % sum)) odds)
        frac-pairs (partition 2 (interleave fracs exprs))]
    `(let [~r (rand)]
       (cond
         ~@(apply concat
                  (reduce
                    (fn [acc [odds expr]]
                      (let [odd-sum (if (seq acc)
                                      (-> acc last first last)
                                      0)]
                        (conj acc [`(< ~r ~(+ odd-sum odds))
                                   expr])))
                    []
                    (drop-last frac-pairs)))
         :else
         ~(-> frac-pairs last last)))))