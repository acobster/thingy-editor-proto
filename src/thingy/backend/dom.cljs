(ns thingy.backend.dom)


(defn update-elem! [elem attrs]
(js/console.log elem (clj->js attrs))
  (doseq [attr attrs]
    (.setAttribute elem (name (key attr)) (val attr))))


(defn update! [{:keys [command attrs] :as event} elem _]
  (cond
    (string? command) (js/document.execCommand command)
    (map? attrs) (update-elem! elem attrs)))


(defonce ^:export backend {:name :dom
                           :update update!})