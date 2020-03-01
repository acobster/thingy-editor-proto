(ns thingy.dom
  (:require
   [clojure.datafy :refer [datafy]]
   [clojure.string :refer [lower-case]]
   [clojure.walk :refer [keywordize-keys]]
   [clojure.core.protocols :as proto]))



    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;                             ;;
  ;;          PROTOCOLS          ;;
 ;;                             ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(declare forward?)
(declare backward?)


(extend-protocol ISeqable
  js/Element
  (-seq [elem] (apply conj
                      [(keyword (lower-case (.-tagName elem)))]
                      (datafy (.-attributes elem))
                      (datafy (.-childNodes elem))))

  js/NodeList
  (-seq [nodes] (vec (js/Array.from nodes)))

  js/NamedNodeMap
  (-seq [m] (vec (js/Array.from m))))

(extend-protocol proto/Datafiable
  js/Element
  (datafy [elem] (seq elem))

  js/Text
  (datafy [text] (str (.-wholeText text)))

  js/NodeList
  (datafy [nodes] (vec (map datafy (seq nodes))))

  js/Selection
  (datafy [selection] {:anchor-node (.-anchorNode selection)
                       :anchor-offset (.-anchorOffset selection)
                       :focus-node (.-focusNode selection)
                       :focus-offset (.-focusOffset selection)
                       :forward? (forward? selection)
                       :backward? (backward? selection)
                       :collapsed? (.-isCollapsed selection)
                       :is-collapsed (.-isCollapsed selection) ; alias
                       :range-count (.-rangeCount selection)
                       :count (.-rangeCount selection) ; alias
                       :type (.-type selection)})

  ;; datafy NamedNodeMaps of Attr objects into Clojure maps
  js/NamedNodeMap
  (datafy [attrs] (keywordize-keys (into {} (map (juxt #(.-name %) #(.-value %))
                                                 (seq attrs))))))

(extend-protocol ICounted
  js/NamedNodeMap
  (-count [m] (.-length m)))

(defprotocol Fragment
  (fragment [x]))

(extend-protocol Fragment
  js/Element
  (fragment [elem] (fragment (.-childNodes elem)))

  js/NodeList
  (fragment [nodes] (vec (concat [:<>] (datafy nodes)))))




    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;                             ;;
  ;;           QUERIES           ;;
 ;;                             ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn all
  ([sel node]
   (.querySelectorAll node sel))
  ([sel]
   (all sel js/document)))

(defn q
  ([sel node]
   (.querySelector node sel))
  ([sel]
   (q sel js/document)))

(defn prev [node]
  (.-previousSibling node))

(defn nxt [node]
  (.-nextSibling node))

(defn attributes [node]
  (.-attributes node))





    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;                             ;;
  ;;           HELPERS           ;;
 ;;                             ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn parent [node]
  (.-parentNode node))

(defn parent? [pnt node]
  (= pnt (.-parentNode node)))

(defn child? [node pnt]
  (= pnt (.-parentNode node)))

(defn ancestor? [ancestor node]
  (.contains ancestor node))

(defn descendant? [node ancestor]
  (.contains ancestor node))


(defn offset [node]
  (loop [n node ct 0]
    (let [pr (prev n)]
      (if (nil? pr)
        ct
        (recur pr (inc ct))))))

(defn path
  "Given a node and its ancestor, finds the path within ancestor to the
   node. Returns the path as a vector, or nil if ancestor does not actually
   contain the node."
  [node ancestor]
  (if-not (and node ancestor (descendant? node ancestor))
    nil
    (letfn [(path* [node ancestor] ; skip the .contains check
              (if (child? node ancestor)
                [(offset node)]
                (conj (path* (parent node) ancestor) (offset node))))]
      (path* node ancestor))))

(defn common-ancestor
  "Finds the common ancestor of all Nodes"
  ([node]
   node)

  ([node relative]
   (loop [n node r relative]
     (if-not (and n r)
       nil
       (let [n>r? (ancestor? n r)]
         (if (or n>r? (ancestor? r n))
           (if n>r? n r)
           (recur (parent n) r))))))
  
  ([node relative & relatives]
   (apply common-ancestor (common-ancestor node relative) relatives)))

(defn lead-offset [node]
  (if (vector? node)
    [0]
    (vec (concat [0] (lead-offset (first node))))))

(lead-offset [:div [:p [:i "asdf" ]]])


(defn tail-offset [node]
  (if (string? node)
    [(dec (count node))]
    (vec (concat [(dec (count node))]
                 (tail-offset (last node))))))



(defn selection
  "Get the current text selection. Calls datafy on the selection object
  by default, returning a map. Pass false to get the raw Selection object."
  []
  (.getSelection js/window))

(defn backward? [sel]
  (> (.-anchorOffset sel) (.-focusOffset sel)))

(defn forward? [sel]
  (> (.-focusOffset sel) (.-anchorOffset sel)))

(defn collapsed? [sel]
  (.-isCollapsed sel))


(comment

  (datafy (selection))
  ;; => {:anchor-offset 0, :range-count 0, :backward? false, :focus-offset 0, :is-collapsed true, :type "None", :count 0, :focus-node nil, :anchor-node nil, :forward? false, :collapsed? true}

  (ed-path (selection root))

  ;;  
  )





    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
   ;;                             ;;
  ;;          MUTATIONS          ;;
 ;;                             ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn select!
  "Set the current user's text selection and cursor position"
  [selection]
  (js/console.log "TODO: select!" selection))
