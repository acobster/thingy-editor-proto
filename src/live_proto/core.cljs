(ns live-proto.core
    (:require
      [reagent.core :as r]))

(defonce appstate
  (r/atom
   {:cursors [{:pos [3 1 25]
               :color "red"}]
    :cursor-colors {"coby" "red"
                    "chuck" "blue"
                    "alex" "green"}}))

;; -------------------------
;; Views

(defn cursor [c]
  [:span.cursor {:style {:border-color (:color c)}}])

(defn place-cursor [html {:keys [pos] :as c}]
  (let [elem-pos (butlast (butlast pos))
        node-pos (last (butlast pos))
        strpos (last pos)
        elem (get-in html (butlast (butlast pos)))
        node (get elem node-pos)
        [elem-left elem-right] (split-at node-pos elem)
        [left-text right-text] (split-at strpos node)
        with-cursor [(apply str left-text)
                     [cursor c]
                     (apply str right-text)]]
    (assoc-in html elem-pos (vec
                             (concat elem-left with-cursor (rest elem-right))))))


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
  (if (make-editable? x) (do (js/console.log "yup" (str (first x))) (make-editable x)) x))


(defn editable [html]
  (vec (map contenteditable (with-cursors html (:cursors @appstate)))))

(defn home-page []
  [editable
   [:div
    [:h2 "Live editing demo"]
    [:p "Officia corrupti quis consectetur dolore nihil reprehenderit consectetur odio voluptatum."]
    [:p "Nihil veniam labore animi magna occaecat. Cillum quos et mollit consequat. Assumenda duis est fuga consectetur nostrud lorem ad."]
    [:h3 "H3 Heading"]
    [:p "Ad proident distinctio eos quo minim et. Repellendus irure eiusmod eligendi tempor enim nobis fuga."]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
