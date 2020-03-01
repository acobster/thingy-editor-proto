(ns thingy.util)

(defn vconcat
  "Concat and return a vector"
  ([] [])
  ([coll] (vec coll))
  ([v & args] (vec (apply concat v args))))

(defn vconj
  "Conj and return a vector"
  ([] [])
  ([coll] (vec coll))
  ([v & args] (vec (apply conj v args))))
