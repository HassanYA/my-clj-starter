(ns app.utils.transformers
  (:require [malli.experimental.time.transform :as mett]
            [malli.transform :as mt]))

(def json-transformer
  (mt/transformer mt/strip-extra-keys-transformer
                  mett/time-transformer
                  mt/json-transformer
                  mt/default-value-transformer))
