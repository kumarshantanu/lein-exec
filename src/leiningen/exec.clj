(ns leiningen.exec
  (:require
   [leiningen.compile :as lc]))


(defn exec
  "Executes a Clojure script"
  [project & args]
  (if (zero? (count args))
    (do
      (println "Usage: lein exec <script-path>")
      (println "Example: lein exec script/run-server.clj"))
    (let [script (first args)
          argstr (pr-str args)]
      (lc/eval-in-project project
                          `(binding [*command-line-args* (read-string ~argstr)]
                             (load-file ~script))))))
