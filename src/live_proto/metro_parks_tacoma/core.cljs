(ns live-proto.metro-parks-tacoma.core
  (:require [reagent.core :as r]))


(defn thingy-editor []
  [:div#thingy-editor {:style {:position "fixed"
                               :top "100vh"
                               :width "100%"
                               :height "100%"
                               :background "transparent"}}])

(defn mount-root [elem]
  (r/render [thingy-editor] elem))

(defn init! []
  (let [app (js/document.createElement "div")]
    (.appendChild js/document.body app)
    (mount-root app)))