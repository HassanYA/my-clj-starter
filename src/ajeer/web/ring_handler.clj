(ns ajeer.web.ring-handler
  (:require [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.coercion.malli]
            [reitit.ring.malli]
            ;; [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [reitit.ring.middleware.muuntaja :refer [format-middleware]]
            [reitit.ring.middleware.exception]
            [malli.util :as mu]
            [muuntaja.core :as muuntaja]
            [ajeer.web.routes :refer [routes]]))

(defn default-error-handler
  "Default safe handler for any exception."
  [^Exception e _]
  (prn e)
  {:status 500
   :body {:type "exception"
          :class (.getName (.getClass e))}})

(defn wrap-database-middleware
  "Return a middleware that associates our database instance to the request map."
  [handler database]
  (fn
    ([request]
     (handler (assoc request :conn (:conn database))))
    ([request respond raise]
     (handler (assoc request :conn (:conn database))
              respond
              raise))))

(defn- make-ring-handler
  [{:keys [dev? db]}]
  (ring/ring-handler
   (ring/router
    [(routes)]
    {:data {:coercion (reitit.coercion.malli/create
                       {:error-keys #{:coercion :in :schema :value :errors :humanized}
                        :compile mu/closed-schema
                        :strip-extra-keys true
                        :default-values true})
            :muuntaja muuntaja/instance
            :middleware  [format-middleware
                          (reitit.ring.middleware.exception/create-exception-middleware {:reitit.ring.middleware.exception/default default-error-handler})
                          coercion/coerce-exceptions-middleware
                          coercion/coerce-request-middleware
                          coercion/coerce-response-middleware
                          [wrap-database-middleware db]]}})))

(defn ring-handler
  [{:keys [dev?] :as opts}]
  (if dev?
    (ring/reloading-ring-handler (partial make-ring-handler opts))
    (make-ring-handler opts)))
