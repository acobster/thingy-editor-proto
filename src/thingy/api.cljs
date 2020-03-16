(ns thingy.api
  (:require
   [thingy.events :as events]))



    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;                                ;;
  ;;             TOOLS              ;;
 ;;                                ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn commander [cmd elem conf]
  (fn []
    (events/emit! {:command cmd} elem conf)))

(defn trigger [t conf & args]
  (let [{:keys [command trigger triggers elem]} t]
    (cond
      (string? command)
      (commander command elem conf)

      (fn? trigger)
      (fn [dom-event]
        (apply trigger dom-event conf args))

      (fn? ((first args) triggers))
      (fn [dom-event]
        (let [f ((first args) triggers)]
          (apply f dom-event conf args)))

      (fn? (:default triggers))
      (fn [dom-event]
        (let [f (:default triggers)]
          (apply f dom-event conf args))))))


(defn label [tool]
  (let [{:keys [name label]} tool]
    (str (or label name))))


(defmulti tool (fn [t editable]
                 (cond
                   (nil? t) (do
                              (js/console.error (str "no tools defined for selector: " (:selector editable)))
                              :default)
                   (keyword? t) t
                   (map? t) (:name t)
                   :else (str t))))

(defmulti render-tool (fn [tool _]
                        (:name tool)))



(defmethod tool :default [t _]
  (js/console.error "not implemented: " (clj->js t)))

(defmethod render-tool :default [tool conf]
  [:button {:on-click (trigger tool conf)}
   (label tool)])