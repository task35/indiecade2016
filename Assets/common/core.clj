(ns common.core
  (:use arcadia.core)
  (:import [UnityEngine Application]))

(defn load-level [name]
  (Application/LoadLevel name))