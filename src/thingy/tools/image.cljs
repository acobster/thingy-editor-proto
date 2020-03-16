(ns thingy.tools.image
  (:require
   [thingy.tools :as tools]
   [thingy.events :as events]))


;; Image picker Tool


(defmethod tools/render-tool :image [tool conf]
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


(defmethod tools/tool :image [t elem]
  (let [tool-defn {:name :image
                   :elem elem
                   :triggers {:pick (fn [_ conf _ attrs]
                                      (let [controlled (or (some-> (:control t) (dom/q))
                                                           elem)]
                                        (events/emit! {:attrs attrs} controlled conf)))}}]
    (cond
      (keyword? t)
      tool-defn

      (map? t)
      (conj t tool-defn))))