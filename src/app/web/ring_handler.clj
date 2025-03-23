(ns app.web.ring-handler
  (:require [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.coercion.malli]
            [reitit.ring.malli]
            [reitit.ring.middleware.muuntaja :refer [format-middleware]]
            [reitit.ring.middleware.exception]
            [ring.middleware.session :refer [wrap-session]]
            [malli.util :as mu]
            [muuntaja.core :as muuntaja]
            [app.web.routes :refer [routes]]
            [app.web.middlewares :refer [wrap-database-middleware
                                         wrap-scookie]]))


(defn default-error-handler
  "Default safe handler for any exception."
  [^Exception e _]
  (prn e)
  {:status 500
   :body {:type "exception"
          :class (.getName (.getClass e))}})

(defn- make-ring-handler
  [{:keys [db cookie]}]
  (ring/ring-handler
   (ring/router
    [(routes)
     ["/assets/*" (ring/create-resource-handler)]]
    {:data {:coercion (reitit.coercion.malli/create
                       {:error-keys #{:coercion :in :schema :value :errors :humanized}
                        :compile mu/closed-schema
                        :strip-extra-keys true
                        :default-values true})
            :muuntaja muuntaja/instance
            :middleware  [[wrap-session cookie]
                          [wrap-scookie cookie]
                          format-middleware
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
