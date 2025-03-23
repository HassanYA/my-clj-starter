(ns app.web.ui.utils
  (:require [hiccup2.core :as h]))

(defn k->name
  [k]
  (if-let [n (namespace k)]
    (str n "_" (name k))
    (name k)))
