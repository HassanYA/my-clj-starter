(ns app.bases.web.middlewares)

(defn wrap-database-middleware
  "Return a middleware that associates our database instance to the request map."
  [handler database]
  (fn
    ([request]
     (handler (assoc request
                     :conn (:ds database))))
    ([request respond raise]
     (handler (assoc request
                     :conn (:ds database))
              respond
              raise))))