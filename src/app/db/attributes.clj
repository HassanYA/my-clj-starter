(ns app.db.attributes)

(def tx-extras
  [{:db/ident :tx/user
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "A ref to a user causing a transaction"}])
