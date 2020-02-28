(ns live-proto.prod
  (:require
    [live-proto.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
