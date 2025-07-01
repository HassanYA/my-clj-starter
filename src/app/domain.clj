(ns app.domain
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as jsql]
            [honey.sql :as sql]
            [honey.sql.helpers :refer [from select]]
            [malli.core :as m]
            [app.schema :as schema]
            [tick.core :as t]))

;; sharks
(def sharks-table :sharks)

(defn get-sharks!
  [conn]
  (jdbc/execute! conn (-> (select :*)
                          (from sharks-table)
                          sql/format)))

(defn get-shark-by-code!
  [conn code]
  (->> (jsql/find-by-keys conn sharks-table {:code code})
       first))

(defn add-shark!
  [conn shark]
  {:pre [(m/validate schema/shark-add shark)]}
  (let [shark (assoc shark :created-at (t/instant))]
    (jsql/insert! conn sharks-table shark)))

(defn patch-shark-by-code!
  [conn code shark]
  (jsql/update! conn sharks-table shark {:code code}))