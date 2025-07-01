(ns app.schema
  (:require [malli.core :as m]
            [malli.util :as mu]
            [app.schema-registry]))

(def shark
  [:map
   [:scientific-name :string]
   [:known-name :string]
   [:code :string]
   [:strength :int]
   [:created-at :time/instant]])

(def shark-add
  (mu/dissoc shark :created-at))

(def shark-patch
  (-> shark
      (mu/dissoc :created-at)
      (mu/dissoc :code)
      (mu/optional-keys)))


