(ns thingy.core
  (:require
   [clojure.datafy :refer [datafy]]
   [reagent.core :as r]
   [thingy.dom :as dom]
   [thingy.util :refer [vconj inject-at]]))


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



(defn- ->contenteditable [elem]
  (vec (assoc-in elem [1 :content-editable] true)))

(defn- ->editable [node root conf]
  ; TODO apply tranformations dynamically according to tools/config
  {:elem (->contenteditable (datafy node))
   :path (dom/path node root)
   :conf conf})

(defn cursor [c]
  [:span.cursor {:style {:border-color (:color c)
                         :background-color (:color c)}}
   [:span.cursor-meta {:style {:color (or (:text-color c) "white")
                               :background-color (:color c)}
                       :content-editable false}
    (:name c)]])

(defn place-cursor [html {:keys [pos] :as c}]
  (let [elem-pos (butlast (butlast pos))
        node-pos (last (butlast pos))
        strpos (last pos)
        elem (get-in html (butlast (butlast pos)))
        node (get elem node-pos)
        [elem-left elem-right] (split-at node-pos elem)
        [left-text right-text] (split-at strpos node)
        with-cursor [(apply str left-text)
                     (cursor c)
                     (apply str right-text)]]
    (assoc-in html elem-pos (vec
                             (concat elem-left with-cursor (rest elem-right))))))

(defn ->with-cursors [fragment cursors]
  (reduce place-cursor fragment cursors))

(->with-cursors [:<> {} [:p {} "aaa bbb"]] [{:pos [2 2 4]}])





(defn tools [ed]
  (:tools (:conf ed)))

(defn query-editables [configs root]
  (set (reduce (fn [editables {:keys [selector] :as conf}]
                 (let [elems (dom/all selector root)
                       these (map #(->editable % root conf) elems)]
                   (concat editables these)))
               []
               configs)))



;; TODO simulate random edits via these cursors
(defonce cursors [{:pos [3 2 8]
                   :color "red"
                   :name "Coby"}])



(defn ^:export editable! [root conf]
  (let [eds (query-editables (:editables conf) root)
        root-fragment (reduce (fn [fragment ed]
                                (assoc-in fragment (:path ed) (:elem ed)))
                              (dom/fragment root)
                              eds)
        decorated (->with-cursors root-fragment cursors)
        state (r/atom {:conf conf
                       :dom-root root
                       :root-content-fragment decorated
                       :editables eds
                       :selection (datafy (dom/selection))
                       :remote-cursors cursors})]
    state))

(defn ^:export mount! [state]
  (r/render (:root-content-fragment @state)
            (:dom-root @state)))
