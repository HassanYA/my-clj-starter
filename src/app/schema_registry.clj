(ns app.schema-registry
  (:require [malli.core :as m]
            [malli.registry :as mr]
            [malli.experimental.time :as met]))

(def default-registry
  (mr/set-default-registry!
   (mr/composite-registry
    (m/default-schemas)
    (met/schemas))))

(comment
  (require '[tick.core :as t])
  (t/instant))
