(ns app.core
  (:require [aero.core :as aero]
            [datomic.api :as d]
            [integrant.core :as ig]
            [org.httpkit.server :refer [run-server]]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [app.web.ring-handler :refer [ring-handler]]))

(defmethod aero/reader 'ig/ref
  [_opts _tag value]
  (ig/ref value))

(defn system-config []
  (aero/read-config (io/resource "config.edn")))

(defmethod ig/init-key :db/primary [_ {:keys [protocol
                                              domain
                                              port
                                              name] :as opts}]
  (let [uri  (format  "%s://%s:%s/%s"
                      protocol domain port name)]
    (log/infof "Starting Datomic at %s" uri)
    (assoc opts
           :conn (do
                   (d/create-database uri)
                   (d/connect uri))
           :uri  uri)))

(defmethod ig/halt-key! :db/primary [_ db]
  (dissoc db :conn :uri))

(defmethod ig/init-key :web/handler [_ opts]
  (ring-handler opts))

(defmethod ig/halt-key! :web/handler [_ _] nil)

(defmethod ig/init-key :web/server [_ opts]
  (log/infof "Starting Web Server at port %s"
             (:port opts))
  (assoc opts :process (run-server (:handler opts)
                                    (dissoc opts :handler))))

(defmethod ig/halt-key! :web/server [_ {:keys [process]}]
  (if process
    (do (log/infof "Halting Web Server in 100ms")
        (process :timeout 100))
    (log/infof "أصلاً مافي سيرفر ولابطيخ")))
