(ns app.web.routes
  (:require [app.schema :as schema]
            [app.web.handlers.shark :as shark]))

(defn routes []
  [["/" {:handler (fn [req] {:body {:msg :hello}})}]
   ["/shark" {:get shark/get-all
              :post {:handler shark/add
                     :parameters {:body schema/shark-add}}}]
   ["/shark/:code" {:get {:handler shark/get-by-code
                          :parameters {:path [:map [:code :string]]}}
                    :patch {:handler shark/patch
                            :parameters {:path [:map [:code :string]]
                                         :body schema/shark-patch}}}]])
