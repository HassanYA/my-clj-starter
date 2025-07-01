(ns app.bases.web.routes
  (:require [app.components.shark.interface.schemas :as shark.schema]
            [app.bases.web.handlers.shark :as shark]))

(defn routes []
  [["/" {:handler (fn [req] {:body {:msg :hello}})}]
   ["/shark" {:get shark/get-all
              :post {:handler shark/add
                     :parameters {:body shark.schema/add}}}]
   ["/shark/:code" {:get {:handler shark/get-by-code
                          :parameters {:path [:map [:code :string]]}}
                    :patch {:handler shark/patch
                            :parameters {:path [:map [:code :string]]
                                         :body shark.schema/patch}}}]])
