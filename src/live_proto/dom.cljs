(ns live-proto.dom
  (:require [clojure.datafy :refer [datafy]]))


(extend-protocol ISeqable
  js/NodeList
  (-seq [nodes] (vec (js/Array.from nodes))))

(extend-protocol Datafiable
  js/NodeList
  (datafy [nodes] (vec (js/Array.from nodes))))


(defn all [sel]
  (.querySelectorAll js/document sel))

(defn q [sel]
  (.querySelector js/document sel))
