(ns app.web.routes)

(defn routes []
  [["/" {:handler (fn [req] {:body {:msg :hello
                                    :conn (:conn req)}})}]])
