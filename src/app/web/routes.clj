(ns app.web.routes)

(defn routes []
  [["/" {:handler (fn [req] {:body {:msg :hello
                                    :conn (:conn req)}})}]
   ["/d" {:post {:handler #(hash-map :body {:params (:parameters %)})
                 :parameters {:body
                              [:map [:d :time/instant]]}}}]])
