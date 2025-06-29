(ns app.core
  (:require [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [org.httpkit.server :refer [run-server]]
            [app.web.ring-handler :refer [ring-handler]]
            [app.config :as config]))

(defmethod ig/init-key :db/primary [_ dbspec]
  (println "Creating DB Connection")
  (assoc dbspec :ds (jdbc/get-datasource dbspec)))

(defmethod ig/halt-key! :db/primary [_ db]
  (println "Halting Connection")
  (dissoc db :ds))

(defmethod ig/init-key :web/handler [_ opts]
  (ring-handler opts))

(defmethod ig/halt-key! :web/handler [_ _] nil)

(defmethod ig/init-key :web/server [_ opts]
  (println "Starting Web Server at port %s"
           (:port opts))
  (assoc opts :process (run-server (:handler opts)
                                   (dissoc opts :handler))))

(defmethod ig/halt-key! :web/server [_ {:keys [process]}]
  (if process
    (do (println "Halting Web Server in 100ms")
        (process :timeout 100))
    (println "أصلاً مافي سيرفر ولابطيخ")))

(defn -main [& args]
  (ig/init (config/system)))
