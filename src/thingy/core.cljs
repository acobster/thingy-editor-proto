(ns thingy.core
  (:require
   [clojure.datafy :refer [datafy]]
   [reagent.core :as r]
   [thingy.dom :as dom]
   [thingy.events :as events]
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


(defn- ->contenteditable [elem state]
  (vec (-> elem
           (assoc-in [1 :content-editable] true)
           (assoc-in [1 :on-key-press] (events/emitter state :contenteditable-keypress)))))

(defn- ->editable [node root conf state]
  ; TODO apply tranformations dynamically according to tools/config
  {:elem (->contenteditable (datafy node) state)
   :path (dom/path node root)
   :conf conf})

(defn cursor [c]
  [:span.cursor {:style {:border-color (:color c)
                         :background-color (:color c)}}
   [:span.cursor-meta {:style {:color (or (:text-color c) "white")
                               :background-color (:color c)}
                       :content-editable false}
    (:name c)]])

;; TODO account for cursor positions when computing new paths
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



(defn tools [ed]
  (:tools (:conf ed)))

(defn query-editables [configs root state]
  (set (reduce (fn [editables {:keys [selector] :as conf}]
                 (let [elems (dom/all selector root)
                       these (map #(->editable % root conf state) elems)]
                   (concat editables these)))
               []
               configs)))



;; TODO do this over real event channels
(defn simulate-remote-cursor-movement! [state max-ms]
  (let [dice (rand-int 10)
        path [:remote-cursors (rand-int 2) :pos 2]]
    (cond
      (= dice 7) (swap! state assoc-in path (rand-int 50))
      (> dice 5) (swap! state update-in path #(max 0 (dec %)))
      :else      (swap! state update-in path #(min (inc %) 80))))

  (js/setTimeout (fn []
                   (simulate-remote-cursor-movement! state max-ms))
                 (rand-int max-ms)))


;; => 



(defn ^:export init-component [root conf]
  (let [state (r/atom {})
        eds (query-editables (:editables conf) root state)
        root-fragment (reduce (fn [fragment ed]
                                (assoc-in fragment (:path ed) (:elem ed)))
                              (dom/fragment root)
                              eds)
        ]
    ;; TODO listen for real update Events on a channel
    ;(simulate-remote-cursor-movement! state 3000)

    (reset! state {:root-content-fragment root-fragment
                   :conf conf
                   :dom-root root
                   :editables eds
                   :caret-pos 0
                   :remote-cursors [{:name "Alice"
                                     :pos [3 2 9]
                                     :color "green"}
                                    {:name "Bob"
                                     :pos [5 2 25]
                                     :color "purple"}]})
    (r/create-class
     {:render
      (fn []
        (->with-cursors (:root-content-fragment @state) (:remote-cursors @state)))

      :component-did-update
      (fn []
        (let [{:keys [dom-root caret-pos]} @state
              focus-node (dom/path->node dom-root (butlast caret-pos))
              sel (dom/selection)
              range (js/document.createRange)]
          (js/console.log (clj->js caret-pos))
          (js/console.log focus-node (last caret-pos))
          ;; TODO why does this fail?
          ;(.setStart range focus-node (last caret-pos))
          (.setStart range focus-node 0)
          (.collapse range true)
          (.removeAllRanges sel)
          (.addRange sel range)))})))


(defn ^:export editable! [root conf]
  (let [component (init-component root conf)]
    (r/render [component] root)))
