(ns app.core
  (:require [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [org.httpkit.server :refer [run-server]]
            [app.web.ring-handler :refer [ring-handler]]
            [app.config :as config]))

;; side-effectful namespaces
(require '[next.jdbc.date-time]
         '[app.schema-registry])

(defmethod ig/init-key :db/primary [_ dbspec]
  (println "Creating DB Connection")
  (let [ds-raw (jdbc/get-datasource dbspec)]
    (assoc dbspec
           :ds-raw ds-raw
           :ds (jdbc/with-options ds-raw config/jdbc-opts))))

(defmethod ig/halt-key! :db/primary [_ db]
  (println "Removing DB Connection")
  (dissoc db :ds :ds-raw))

(defmethod ig/init-key :web/handler [_ opts]
  (ring-handler opts))

(defmethod ig/halt-key! :web/handler [_ _] nil)

(defmethod ig/init-key :web/server [_ opts]
  (println (format "Starting Web Server at port %s"
                   (:port opts)))
  (assoc opts :process (run-server (:handler opts)
                                   (dissoc opts :handler))))

(defmethod ig/halt-key! :web/server [_ {:keys [process]}]
  (if process
    (do (println "Halting Web Server in 100ms")
        (process :timeout 100))
    (println "أصلاً مافي سيرفر ولابطيخ")))

(defn -main [& args]
  (ig/init (config/system)))
