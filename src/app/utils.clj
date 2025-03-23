(ns app.utils)

(defn assoc-if
  ([m attr val] (assoc-if m attr val val))
  ([m attr val condition] (if condition (assoc m attr val) m)))

(defn if-else
  [cond else]
  (if cond cond else))
