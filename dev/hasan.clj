(ns hasan
  (:require [integrant.repl :refer [clear go halt prep init reset reset-all]]
            [integrant.repl.state :as state]))

(integrant.repl/set-prep! (fn []
                            ((requiring-resolve 'ajeer.core/system-config))))

(defn system [] (or state/system
                    (throw (ex-info "System not running" {}))))


(comment
  (prep)
  (init)
  (halt)
  (system))
