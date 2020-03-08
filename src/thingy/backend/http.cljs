(ns thingy.backend.http
  (:require
   [cljs-http.client :as http]
   [thingy.dom :as dom]))


(defn conf->endpoint [{:keys [endpoint] :as http-conf} event elem]
  (cond
    (string? endpoint) endpoint
    (fn? endpoint) (endpoint event elem)
    (nil? http-conf) (js/console.warn "No HTTP backend config detected")
    :else (js/console.warn "No endpoint configured" (clj->js http-conf))))

(defn event->request [event elem conf]
  (let [ident (.-thingyIdent (dom/dataset elem))]
    {:method "POST"
     :headers {"Content-Type" "application/json"}
     :body (.stringify js/JSON (clj->js {:ident ident
                                         :content (.-innerHTML elem)}))}))


(defn ^:export update! [event elem conf]
  (let [req (event->request event elem conf)
        promise (js/fetch (conf->endpoint (:http conf) event elem) (clj->js req))]
    (.then promise
           (fn [response]
             (js/console.log response)))))


(defonce ^:export backend {:name :dom
                           :update update!})