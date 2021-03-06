(ns thingy.core
  (:require
   [clojure.walk :refer [keywordize-keys]]
   [reagent.core :as r]
   [thingy.dom :as dom]
   [thingy.events :as events]
   [thingy.ui :as ui]
   [thingy.tools :as tools]
   [thingy.tools.image]
   [thingy.tools.text]))


(defmethod tools/tool :default [t _]
  (js/console.error "not implemented: " (clj->js t)))

(defmethod tools/render-tool :default [tool conf]
  [:button {:on-click (tools/trigger tool conf)}
   (tools/label tool)])


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
  (and elem (not (false? (:contenteditable ed)))))

(defn contenteditable! [node]
  (.setAttribute node "contenteditable" true))


(defn listen-for-interactions! [{:keys [elem conf]} editor-state global-conf]
  (letfn [(open-editor! [_]
                        (swap! editor-state
                               (fn [state]
                                 (-> state
                                     (assoc :elem elem)
                                     (assoc :tools (map (fn [t]
                                                          (tools/tool t elem))
                                                        (:tools conf)))))))]
   (.addEventListener elem
                      "keyup"
                      (fn [e]
                        (events/emit! e elem global-conf)))
   (.addEventListener elem "focus" open-editor!)
   (.addEventListener elem "mouseenter" open-editor!)))


(defn ^:export editable! [root conf] 
  (let [conf (keywordize-keys conf)
        editor-state (r/atom {:tools []
                              :backends (:backends conf)})
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
