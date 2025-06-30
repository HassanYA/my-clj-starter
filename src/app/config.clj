(ns app.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [integrant.core :as ig]
            [next.jdbc.result-set :refer [as-unqualified-kebab-maps]]))

(defmethod aero/reader 'ig/ref
  [_opts _tag value]
  (ig/ref value))

(defn system []
  (aero/read-config (io/resource "config.edn")))

(def jdbc-opts
  {:builder-fn as-unqualified-kebab-maps
   :table-fn #(string/replace % #"-" "_")
   :column-fn #(string/replace % #"-" "_")})
