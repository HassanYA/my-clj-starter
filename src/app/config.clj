(ns app.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [integrant.core :as ig]))

(defmethod aero/reader 'ig/ref
  [_opts _tag value]
  (ig/ref value))

(defn system []
  (aero/read-config (io/resource "config.edn")))