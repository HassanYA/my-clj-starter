(ns app.components.shark.interface.schemas
  (:require [malli.util :as mu]
            [app.schema-registry]))

(def base
  [:map
   [:scientific-name :string]
   [:known-name :string]
   [:code :string]
   [:strength :int]
   [:created-at :time/instant]])

(def add
  (mu/dissoc base :created-at))

(def patch
  (-> base
      (mu/dissoc :created-at)
      (mu/dissoc :code)
      (mu/optional-keys)))