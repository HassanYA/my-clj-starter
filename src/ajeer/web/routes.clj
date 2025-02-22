(ns ajeer.web.routes)

(defn routes []
  [["/" {:handler (fn [req] (println (req :db)) {:body {:msg :reloaded}})}]])
