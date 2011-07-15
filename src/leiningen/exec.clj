(ns leiningen.exec
  (:require
   [leiningen.compile :as lc]))

(defn exec
  "Executes a Clojure script"
  [project & args]
  (if (not= 1 (count args))
    (do
      (println "Usage: lein exec <script-path>")
      (println "Example: lein exec script/run-server.clj"))
    (let [script (first args)]
      (lc/eval-in-project project
                          `(load-file ~script)))))
