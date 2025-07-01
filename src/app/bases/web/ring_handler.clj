(ns app.bases.web.ring-handler
  (:require [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.coercion.malli]
            [reitit.ring.malli]
            [reitit.ring.middleware.muuntaja :refer [format-middleware]]
            [reitit.ring.middleware.exception]
            [malli.util :as mu]
            [muuntaja.core :as muuntaja]
            [app.bases.web.routes :refer [routes]]
            [app.bases.web.middlewares :refer [wrap-database-middleware]]
            [app.utils.transformers :refer [json-transformer]]))


(defn default-error-handler
  "Default safe handler for any exception."
  [^Exception e _]
  (prn e)
  {:status 500
   :body {:type "exception"
          :class (.getName (.getClass e))}})


(defn- make-ring-handler
  [{db :db :as opts}]
  (ring/ring-handler
   (ring/router
    [(routes)
     ["/assets/*" (ring/create-resource-handler)]]
    {:data {:coercion (reitit.coercion.malli/create
                       (-> reitit.coercion.malli/default-options
                           (merge {:error-keys #{:coercion :in :schema :value :errors :humanized}
                                   :compile mu/closed-schema
                                   :strip-extra-keys true
                                   :default-values true})
                           (assoc-in [:transformers :body :formats "application/json"]
                                     json-transformer)
                           (assoc-in [:transformers :response :formats "application/json"]
                                     json-transformer)))
            :muuntaja muuntaja/instance
            :middleware  [format-middleware
                          (reitit.ring.middleware.exception/create-exception-middleware {:reitit.ring.middleware.exception/default default-error-handler})
                          coercion/coerce-exceptions-middleware
                          coercion/coerce-request-middleware
                          coercion/coerce-response-middleware
                          [wrap-database-middleware db]]}})
   (ring/create-default-handler)))

(defn ring-handler
  [{:keys [dev?] :as opts}]
  (if dev?
    (ring/reloading-ring-handler (partial make-ring-handler opts))
    (make-ring-handler opts)))
