(ns thingy.backend.dom)


(defn update-elem! [elem attrs]
(js/console.log elem (clj->js attrs))
  (doseq [attr attrs]
    (.setAttribute elem (name (key attr)) (val attr))))


(defn update! [event elem _]
  (let [{:keys [command attrs]} event]
    (cond
      (string? command) (js/document.execCommand command)
      (map? attrs) (update-elem! elem attrs))))


(defonce ^:export backend {:name :dom
                           :update update!})