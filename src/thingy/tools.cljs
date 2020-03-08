(ns thingy.tools
  (:require
   [thingy.dom :as dom]
   [thingy.events :as events]))


;; TOOLS are an abstract representation of event triggers.
;; Tools turn DOM Events into values.
;; This namespace does not expose tools in the UI directly;
;; only the underlying fns that the UI calls.


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

(defmethod tool :default [t _]
  (js/console.error "not implemented: " (clj->js t)))


(defmethod tool :b [_ elem]
  {:label "B"
   :name :bold
   :command "bold"
   :elem elem})

(defmethod tool :bold [_ elem] (tool :b elem))


(defmethod tool :i [_ elem]
  {:label "I"
   :name :italic
   :command "italic"
   :elem elem})

(defmethod tool :italic [_ elem] (tool :i elem))


(defmethod tool :u [_ elem]
  {:label "U"
   :name :underline
   :command "underline"
   :elem elem})

(defmethod tool :underline [_ elem] (tool :u elem))


(defmethod tool :image [t elem]
  (conj t {:name :image
           :elem elem
           :triggers {:pick (fn [_ conf _ attrs]
                              (let [controlled (or (some-> (:control t) (dom/q))
                                                   elem)]
                                (events/emit! {:attrs attrs} controlled conf)))}}))