(ns thingy.tools
  (:require
   [thingy.events :as events]))


;; Tools are an abstract representation of event triggers. Tools turn DOM Events into values.
;; This namespace defines the core API for implementing and rendering Tools; other namespaces
;; extend this API to define specific tool implementations.


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



    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;                             ;;
  ;;           HELPERS           ;;
 ;;                             ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


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