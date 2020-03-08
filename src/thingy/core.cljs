(ns thingy.core
  (:require
   [clojure.walk :refer [keywordize-keys]]
   [reagent.core :as r]
   [thingy.dom :as dom]
   [thingy.events :as events]
   [thingy.tools :as tools]
   [thingy.ui :as ui]))



(defn- ->editable [node ed-conf]
  (conj ed-conf {:elem node
                 :conf ed-conf}))


(defn query-editables [root configs]
  (set (reduce (fn [editables {:keys [selector] :as ed-conf}]
                 (let [elems (dom/all selector root)
                       these (map #(->editable % ed-conf) elems)]
                   (concat editables these)))
               []
               configs)))


(defn node->state [root {editables :editables :as conf}]
  (let [eds (query-editables editables root)
        state (r/atom {:conf conf
                       :dom-root root
                       :editables eds})]
    state))

(defn make-contenteditable? [elem ed]
(js/console.log (:contenteditable ed))
  (and elem (not (false? (:contenteditable ed)))))

(defn contenteditable! [node]
  (.setAttribute node "contenteditable" true))


(defn listen-for-interactions! [{:keys [elem conf]} editor-state global-conf]
  (.addEventListener elem
                     "keyup"
                     (fn [e]
                       (events/emit! e elem global-conf)))
  (.addEventListener elem
                     "mouseenter"
                     #(swap! editor-state
                             (fn [state]
                               (-> state
                                   ; TODO let caller implement own positioning logic
                                   (assoc :pos (ui/relative-position elem))
                                   (assoc :tools (map (fn [t]
                                                        (tools/tool t elem))
                                                      (:tools conf))))))))


(defn ^:export editable! [root conf] 
  (let [conf (keywordize-keys conf)
        editor-state (r/atom {:tools []
                              :backends (:backends conf)
                              :pos [0 0]}) ; THIS WILL CHANGE PER ELEMENT
        ; set up an editor state
        ; hook tool events to backends in conf
        ; TODO add dom-backend by default
        editables (query-editables root (:editables conf))]
    (doseq [{elem :elem :as ed} editables]
      (when (make-contenteditable? elem ed)
        (contenteditable! elem))
      ;; Listen for hover/mouseover events
      (listen-for-interactions! ed editor-state conf))
    (ui/mount! editor-state conf)))
