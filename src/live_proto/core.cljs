(ns live-proto.core
    (:require
     [thingy.core :as t]
     [thingy.dom :as dom]
     [reagent.core :as r]))


;; 
;; When the user makes an edit (types a key, makes a selection, etc.), we want to
;; record that as a cursor movement and store their current editing state so that
;; when the DOM gets re-rendered (upon some externally-triggered state change, such
;; as a multiplayer cursor moving) they don't lose their place, and can keep editing
;; as though nothing has happened.
;; 
;; To that end, we need to model all possible changes to the user's current editing
;; environment. These include:
;; 
;;  * changes to the DOM such as other editors' cursor movements or arbitrary edits,
;;    such as Elements being added or removed.
;;  * changes to the user's cursor position or text selection
;; 
;; To do this, we keep track of the current selection data as a map inside the
;; appstate atom:
;; 
;;   (:cursor @appstate) ; => {:anchor-offset 17, :range-count 1, ,,,}
;;
;; We also need to keep track of the path to each DOM node that may mutate during
;; the lifetime of our application. This path is just a vector of numbers and is
;; intended to be passed to get-in, assoc-in, and related functions along with a
;; vector representing the common ancestor of all DOM nodes potentially being
;; edited. That common ancestor is also stored within our appstate so we can 
;; refer to it anytime.
;; 
;; Events we need to datafy and apply in order:
;; 
;;  * the user placed their cursor
;;  * the user selected something
;;  * the user typed something
;;  * the user deleted some text
;;  * the user inserted or deleted a field
;;  * the user executed a tool (bold, italicize, etc.)
;;  * any of the above, but for a collaborator (remote multiplayer user)
;;  * a remote user joins the collab session
;;  * a remote user becomes idle
;;  * a revision is saved or published
;; 


(def appstate
  (t/editable! (dom/q "#editable-container") {:editables [{:selector "h2,h3"}
                                                          {:selector "p" :foo 'bar}]}))


;; -------------------------
;; Initialize app

(defn mount-root []
  ; TODO this is creating a duplicate #editable-container - it shouldn't do that.
  (r/render (:root-content-fragment @appstate) (:dom-root @appstate)))

(defn ^:export init! []
  (mount-root))

