(ns thingy.events
  (:require
   [clojure.datafy :refer [datafy]]
   [thingy.dom :as dom]))




(defn contenteditable-updated [state]
  (let [root (:dom-root @state)
        {:keys [focus-node focus-offset anchor-offset]} (datafy (dom/selection))
        path (conj (dom/path focus-node root) anchor-offset)]
    (swap! state assoc :caret-pos path)))




(defn- emit! [ratom event & args]
  (let [handler (event {:contenteditable-keypress contenteditable-updated})]
    (apply handler ratom args)))

(defn emitter [ratom event]
  (fn [& args]
    (apply emit! ratom event args)))