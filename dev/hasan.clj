(ns hasan
  (:require [integrant.repl :refer [clear go halt prep init reset reset-all]]
            [integrant.repl.state :as state]
            [ragtime.next-jdbc :as rg-jdbc]
            [ragtime.repl :as rg-repl]
            [next.jdbc.sql :as jsqls]
            [tick.core :as t]
            [app.domain :as domain]))

(require '[app.core])

(integrant.repl/set-prep! (fn []
                            ((requiring-resolve 'app.config/system))))

(defn system [] (or state/system
                    (throw (ex-info "System not running" {}))))

(def conn
  #(-> (system) :db/primary :ds))




(comment
  ;; integrant
  (prep)
  (init)
  (halt)

  ;; inspect system components
  (system)

  ;; ragtime migrations
  (def rg-conf
    #(hash-map :datastore (rg-jdbc/sql-database (conn))
               :migrations (rg-jdbc/load-resources "migrations")))
  (rg-repl/migrate (rg-conf))
  (rg-repl/rollback (rg-conf)))

