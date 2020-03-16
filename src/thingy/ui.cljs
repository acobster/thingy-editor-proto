(ns thingy.ui
  (:require
   [clojure.datafy :refer [datafy]]
   [reagent.core :as r]
   [thingy.tools :as tools]
   [thingy.dom :as dom]))


;; UI is the visual interface for interacting with Tools. UI does not perform any DOM mutations
;; on any editables. It merely dispatches DOM Events to the Tools layer, which in turn
;; translates them into values for the DOM Backend and any other backends to interpret.
;; This namespace does not define any specific tool UIs; it delegates that work to the Tools
;; layer, which defines the core API and implementation for all Tools.


(defn- toolbar-mount-point []
  (doto (.createElement js/document "div")
    (aset "id" (gensym "toolbar--"))))

;; TODO use herb or spade for inline styles
;; https://github.com/roosta/herb
;; https://github.com/dhleong/spade
(defn editor->style [editor-state]
  (let [{:keys [x y height]} (some-> (:elem editor-state) dom/bounding-rect datafy)]
    {:position "absolute"
     :top (str (+ y height) "px")
     :left (str x "px")
     :max-width "100%"
     :background-color "blue"}))

(defn ^:export default-ui [editor-state conf]
  (let [tools (:tools @editor-state)
        display? (> (count tools) 0)
        ->style (or (:editor->style @editor-state) editor->style)]
    ; TODO make toolbar rendering pluggable
    (if display?
      [:div.toolbar {:style (->style @editor-state)}
       (map-indexed (fn [i t]
                      ^{:key i}
                      [tools/render-tool t conf])
                    tools)]
      [:div {:style {:display "none"}}])))


(defn- toolbar [editor-state conf]
  (let [component (or (:ui conf) default-ui)]
    [component editor-state conf]))


(defn mount! [editor-state conf]
  (let [div (toolbar-mount-point)]
    (.appendChild js/document.body div)
    (r/render [toolbar editor-state conf] div)))