(ns app.web.middlewares
  (:require [datomic.api :as d]
            [starfederation.datastar.clojure.api :refer [with-open-sse]]
            [starfederation.datastar.clojure.adapter.http-kit :refer [->sse-response]]
            [ring.middleware.session :refer [session-request]]))

(defn wrap-database-middleware
  "Return a middleware that associates our database instance to the request map."
  [handler database]
  (fn
    ([request]
     (handler (assoc request
                     :conn (:conn database)
                     :db (d/db (:conn database)))))
    ([request respond raise]
     (handler (assoc request
                     :conn (:conn database)
                     :db (d/db (:conn database)))
              respond
              raise))))

;; (defn get-user [req db]
;;   (when-let [id (get-in req [:session :db/id])]
;;     (let [user (d/pull (d/db (:conn db))
;;                        (:default usr/patterns)
;;                        id)]
;;       (when (:db/id user) user))))

;; (defn wrap-user
;;   [handler database scookie]
;;   (fn
;;     ([request]
;;      (handler (assoc request
;;                      :user
;;                      (get-user (session-request request
;;                                                 scookie)
;;                                database))))
;;     ([request respond raise]
;;      (handler (assoc request
;;                      :user
;;                      (get-user (session-request request
;;                                                scookie)
;;                                database))
;;               respond
;;               raise))))

(defn wrap-scookie
  [handler scookie]
  (fn [request]
    (handler (assoc request
                    :scookie scookie))))

(defn sse-middleware
  [handler]
  (fn
    ([request]
     (let [{:keys [on-open on-close]} (handler request)]
       (->sse-response request
                       (if on-close
                         {:on-close on-close
                          :on-open #(with-open-sse % (on-open %))}
                         {:on-open #(with-open-sse % (on-open %))}))))))

(defn with-open-sse-middleware
  [handler]
  (fn [request]
    (->sse-response request
                    {:on-open #(with-open-sse % ((handler request) %))})))
