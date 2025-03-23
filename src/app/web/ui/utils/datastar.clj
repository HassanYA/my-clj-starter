(ns app.web.ui.utils.datastar
  (:require [clojure.string :refer [join]]
            [cheshire.core :as json]
            [starfederation.datastar.clojure.api :refer [get-signals execute-script!]]
            [clojure.walk :refer [keywordize-keys]]))

(defn ->bind
  [k]
  (format "%s"
          (str
           (when-let [ns* (namespace k)] (str ns* "/")) (name k))))

(def ->b ->bind)
(defn ->$ [k] (->> k ->b (str "$")))

(declare ->signal)

(defn ->str
  [item]
  (cond
    (keyword? item) (format "'%s'" (->bind item))
    (string? item) (str "'" item "'")
    (map? item) (->signal item)
    (coll? item) (format "[%s]"
                  (->> (map ->str item)
                       (join " , ")))
    :else (str item)))

(defn ->signal
  [m]
  (format "{%s}"
          (->> m
               (map #(str (->str (first %)) ": " (->str (second %))))
               (join " , "))))

(def ->* ->signal)

(defn ->class-value
  [s]
  (condp #(%1 %2) s
    keyword? (str "$" (->bind s))
    identity s))

(defn ->*c
  [m]
  (format "{%s}"
          (->> m
               (map #(str (->str (first %)) ": " (->class-value (second %))))
               (join " , "))))

(defn signal->map
  [req]
  (if-let [params (:params req)]
    (keywordize-keys params)
    (-> req
        get-signals
        json/parse-string)))


(defn reload!
  "Redirect a page using a script."
  ([sse-gen]
   (reload! sse-gen {}))
  ([sse-gen opts]
   (execute-script! sse-gen "window.location.reload();" opts)))
