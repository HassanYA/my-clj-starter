(ns app.db.setup
  (:require [datomic.api :as d]
            [ajeer.db.attributes :as attrs]))


(defn init!
  [conn]
  [@(d/transact conn (concat
                             attrs/tx-extras))])
