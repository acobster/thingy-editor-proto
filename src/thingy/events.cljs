(ns thingy.events)









(defn- emit! [state event & args]
  (js/console.log (str (name event) " event emitted")))

(defn emitter [state event]
  (fn [& args]
    (apply emit! state event args)))