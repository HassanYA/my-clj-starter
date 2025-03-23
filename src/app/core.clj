(ns app.core
  (:require [aero.core :as aero]
            [datomic.api :as d]
            [integrant.core :as ig]
            [org.httpkit.server :refer [run-server]]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [app.web.ring-handler :refer [ring-handler]]
            [app.db.setup :as db-setup]
            [ring.middleware.session.cookie :refer [cookie-store]])
  (:import [org.apache.commons.codec.binary Base64]))

(defmethod aero/reader 'ig/ref
  [_opts _tag value]
  (ig/ref value))

(defn config []
  (aero/read-config (io/resource "config.edn")))

(defmethod ig/init-key :db/primary [_ {:keys [protocol
                                              domain
                                              port
                                              name] :as opts}]
  (let [uri  (format  "%s://%s:%s/%s"
                      protocol domain port name)
        conn (do
                   (d/create-database uri)
                   (d/connect uri))]
    (println (format "Connected to Datomic at %s" uri))
    (db-setup/init! conn)
    (println (format "Datomic seeded!!"))
    (assoc opts
           :conn conn
           :uri  uri)))

(defmethod ig/halt-key! :db/primary [_ db]
  (dissoc db :conn :uri))

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

(defmethod ig/init-key :web/session [_ opts]
  (assoc opts
         :store
         (cookie-store {:key (Base64/decodeBase64 (opts :secret))})))

(defmethod ig/halt-key! :web/session [_ _] nil)

(defn -main [& args]
  (ig/init (config)))
