(ns thingy.ui
  (:require
   [clojure.datafy :refer [datafy]]
   [reagent.core :as r]
   [thingy.tools :as tools]
   [thingy.dom :as dom]))


;; UI is the visual interface for interacting with Tools.
;; UI does not perform any DOM mutations on any editables.
;; It merely dispatches DOM Events to the Tools layer,
;; which in turn translates them into values for the DOM Backend
;; and any other backends to interpret.


(defmulti render-tool (fn [tool _]
                        (:name tool)))

(defmethod render-tool :default [tool conf]
  [:button {:on-click (tools/trigger tool conf)}
   (tools/label tool)])


(defmethod render-tool :image [tool conf]
  (let [library (:library tool)]
    [:div
     [:h3 "IMAGE GALLERY"]
     [:div {:style {:overflow "scroll"}}
      (map-indexed (fn [idx {:keys [uri alt]}]
                     ^{:key idx}
                     [:div.image-choice {:style {:margin "3px"}}
                      [:img {:src uri
                             :alt alt
                             :on-click (tools/trigger tool conf :pick {:src uri
                                                                       :alt alt})}]])
                   library)]]))



(defn- toolbar-mount-point []
  (let [div (.createElement js/document "div")]
    (.setAttribute div "id" (gensym "toolbar--"))
    div))

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
        display? (> (count tools) 0)]
    ; TODO make toolbar rendering pluggable
    (if display?
      [:div.toolbar {:style (editor->style @editor-state)}
       (map-indexed (fn [i t]
                      ^{:key i}
                      [render-tool t conf])
                    tools)]
      [:div {:style {:display "none"}}])))


(defn- toolbar [editor-state conf]
  (let [component (or (:ui conf) default-ui)]
    [component editor-state conf]))


(defn mount! [editor-state conf]
  (let [div (toolbar-mount-point)]
    (.appendChild js/document.body div)
    (r/render [toolbar editor-state conf] div)))