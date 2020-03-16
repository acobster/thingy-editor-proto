(ns thingy.tools.text
  (:require
   [thingy.dom :as dom]
   [thingy.tools :as tools]))


;; Text-based tools for basic WYSIWYG-like functionality

(defmethod tools/tool :b [_ elem]
  {:label "B"
   :name :bold
   :command "bold"
   :elem elem})

(defmethod tools/tool :bold [_ elem] (tools/tool :b elem))


(defmethod tools/tool :i [_ elem]
  {:label "I"
   :name :italic
   :command "italic"
   :elem elem})

(defmethod tools/tool :italic [_ elem] (tools/tool :i elem))


(defmethod tools/tool :u [_ elem]
  {:label "U"
   :name :underline
   :command "underline"
   :elem elem})

(defmethod tools/tool :underline [_ elem] (tools/tool :u elem))