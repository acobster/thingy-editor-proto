(ns thingy.backend.http
  (:require
   [cljs-http.client :as http]
   [thingy.dom :as dom]))


(defn ->endpoint [event elem {:keys [endpoint] :as http-conf}]
  (cond
    (string? endpoint) endpoint
    (fn? endpoint) (endpoint event elem)
    (nil? http-conf) (js/console.warn "No HTTP backend config detected")
    :else (js/console.warn "No endpoint configured" (clj->js http-conf))))

(defn event->request [event elem _]
  {:json-params {:ident (.-thingyIdent (dom/dataset elem))
                 :attrs (:attrs event)
                 :content (.-innerHTML elem)}})


(defn ^:export update! [event elem conf]
  (let [http-conf (:http conf)
        ->req (or (:event->request http-conf) event->request)
        method (or (:method http-conf) http/post)
        endpoint (->endpoint event elem http-conf)
        req (->req event elem conf)]
    (method endpoint req)))


(defonce ^:export backend {:name :dom
                           :update update!})