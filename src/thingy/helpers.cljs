(ns thingy.helpers)


;(defonce appstate
;  (r/atom
;   {:selection {}
;    :remote-cursors [{:pos [3 1 25]
;                      :color "red"}]
;    :cursor-colors {"coby" "red"
;                    "chuck" "blue"
;                    "alex" "green"}
;    :editables [{:h [:h2 "Live editing demo"]}
;                {:h [:p "Officia corrupti quis consectetur dolore nihil reprehenderit consectetur odio voluptatum."]}
;                {:h [:p "Nihil veniam labore animi magna occaecat. Cillum quos et mollit consequat. Assumenda duis est fuga consectetur nostrud lorem ad."]}
;                {:h [:h3 "H3 Heading"]}
;                {:h [:p "Ad proident distinctio eos quo minim et. Repellendus irure eiusmod eligendi tempor enim nobis fuga."]}]}))

;; -------------------------
;; Views

(defn cursor [c]
  [:span.cursor {:data-color "green"
                 :style {:color (:color c)
                         :border-color (:color c)}}])


(defn- inject-at [n coll x]
  (let [[left right] (split-at n coll)]
    (concat left [x] right)))

(defn- element? [x]
  (and (vector? x) (keyword? (first x))))

(defn- has-attrs-map? [elem]
  (and (element? elem) (map? (second elem))))

(defn- normalize-attrs [elem]
  (if (has-attrs-map? elem)
    elem
    (vec (inject-at 1 elem {}))))


(defn with-cursors [html cursors]
  (reduce place-cursor html cursors))

(defn make-editable?
  "Determine whether to make the given element contenteditable or not"
  [elem]
  (and (element? elem)
       (contains? #{:h2 :h3 :p} (first elem))))

(defn make-editable [elem]
  (vec (assoc-in (normalize-attrs elem) [1 :content-editable] true)))

(defn contenteditable [x]
  (if (make-editable? x) (make-editable x) x))


;(defn editable [html]
;  (with-cursors html (:remote-cursors @appstate)))
;
;(defn home-page []
;  [editable
;   (vec (concat [:div.app-containter] (map :h (:editables @appstate))))])