(ns thingy.core
  (:require
   [clojure.datafy :refer [datafy]]
   [reagent.core :as r]
   [thingy.dom :as dom]
   [thingy.helpers :as help]
   [thingy.util :refer [vconj]]))


(defprotocol EditablePath
  (path [this root]))

(extend-protocol EditablePath
  js/Element
  (path [this root] (dom/path this root))
  
  js/Text
  (path [this root] (dom/path this root)))


(defprotocol EditEvent
  (edit-event [this root] [this root args]))

(extend-protocol EditEvent
  js/Selection
  (edit-event
   [this root]
   (let [data (datafy this)
         {:keys [anchor-node anchor-offset focus-node focus-offset]} data]
     {:within-root? (and
                     (dom/ancestor? root anchor-node)
                     (dom/ancestor? root focus-node))
      :anchor-path  (vconj (dom/path anchor-node root) anchor-offset)
      :focus-path   (vconj (dom/path focus-node root) focus-offset)})))




(defn query-editables [editables root]
  (into {} (map (juxt #(dom/path % root) identity)
                (reduce (fn [eds ed]
                          (concat eds (-> ed :selector (dom/all root))))
                        []
                        editables))))

(defn ^:export editable! [root conf]
  (let [eds (query-editables (:editables conf) root)
        state (r/atom {:conf conf
                       :dom-root root
                       :root-content-fragment (dom/fragment root)
                       :paths->nodes eds
                       :selection (datafy (dom/selection))
                       :remote-cursors []})]
    (.addEventListener root "click" (fn [e]
                                      (js/console.log (.-target e))))
    (.addEventListener root "selectionchange" (fn [e]
                                                (js/console.log (.-target e))))
    (js/console.log (clj->js (datafy root)))
    state))

(comment

  (editable! (dom/q "#editable-container")
             ;; TODO
             ;; * images
             {:editables [{:selector "h2,h3"
                           :tools [:b :u :i]}
                          {:selector "p"
                           :tools [:b :u :i :img :repeat]}]
              ;; specify design rules:
              ;; - <h2>s and <h3>s come as a package deal, one after the other
              ;; - a <p> can only come after an <h3>
              :design-rules [[:h2 :must :precede :h3]
                             [:h3 :must :follow :h2]
                             [:p :must :follow :h3]]})

  ;;  
  )