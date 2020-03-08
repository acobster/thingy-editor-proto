(ns thingy.events)


;; THINGY EVENTS are triggered by tools themselves, in response to UI/DOM Events

(defn emit! [event elem {:keys [backends] :as conf}]
  (doseq [b backends]
    (let [up (:update b)]
      (if (fn? up)
        (up event elem conf)
        (js/console.warn "backend update is not a function" (clj->js b))))))
