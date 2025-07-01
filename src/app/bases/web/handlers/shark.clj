(ns app.bases.web.handlers.shark
  (:require [app.components.shark.interface.io :as io]))

(defn get-all
  [req]
  {:status 200
   :body {:list (io/get-sharks! (-> req :conn))}})

(defn get-by-code
  [req]
  (if-let [shark (io/get-shark-by-code! (-> req :conn)
                                        (-> req :parameters :path :code))]
    {:status 200, :body shark}
    {:status 404}))

(defn add
  [req]
  (if (io/get-shark-by-code! (-> req :conn)
                             (-> req :parameters :body :code))
    {:status 409
     :body {:message "Shark code is not unique"}}
    {:status 201
     :body (io/add-shark! (-> req :conn)
                          (-> req :parameters :body))}))

(defn patch
  [{:keys [conn]
    {{:keys [code]} :path, shark :body} :parameters}]
  (if-let [curr-shark (io/get-shark-by-code! conn code)]
    {:status 200
     :body (do (io/patch-shark-by-code! conn code shark)
               (merge curr-shark shark))}
    {:status 404}))