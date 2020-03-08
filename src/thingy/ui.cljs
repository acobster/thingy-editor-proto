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

(defn relative-position [elem]
  (let [{:keys [x y]} (some-> elem dom/bounding-rect datafy)]
    [x y]))

(defn ^:export default-ui [editor-state conf]
  (let [elem (:elem @editor-state)
        w (some-> elem dom/bounding-rect datafy :width)
        [x y] (relative-position elem)
        tools (:tools @editor-state)
        display? (> (count tools) 0)]
    ; TODO make toolbar rendering pluggable
    (if display?
      [:div.toolbar {:style {:position "absolute"
                             :bottom (str "calc(" y "px - 5px)")
                             :left x
                             :width w
                             :max-width "50%"
                             :background-color "lightgray"}}
       [:h4 "TOOLBAR"]
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