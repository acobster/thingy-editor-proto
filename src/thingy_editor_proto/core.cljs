(ns thingy-editor-proto.core
    (:require
     [thingy.core :as t]
     [thingy.dom :as dom]
     [thingy.backend.dom :as dom-backend]
     [thingy.backend.http :as http-backend]))


;; -------------------------
;; Initialize app

(defn ^:export init! []
  (t/editable! (js/document.getElementById "editable-container")
               {:editables [{:selector "h2,h3"
                             :tools []}
                            {:selector "p"
                             :tools [:b :i :u]}
                            {:selector "img"
                             :tools [:image]}]
                :backends [dom-backend/backend
                           http-backend/backend]
                :http {:endpoint (fn []
                                   "/my-api")}})
  (doto (dom/q "p:last-of-type")
    (.dispatchEvent (js/Event. "focus"))))

