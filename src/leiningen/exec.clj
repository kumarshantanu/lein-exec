(ns leiningen.exec
  (:require [leiningen.core.eval  :as eval]
            [cemerick.pomegranate :as pome]))


(defn deps
  "Pull `the-deps` dependencies from Maven Central and Clojars. This is a
  convenience function for scripts executed by this plugin.
  Example:
    (use '[leiningen.exec :only (deps)])
    (deps '[[compojure \"1.0.1\"]
            [org.clojure/java.jdbc \"0.1.0\"]])"
  [the-deps]
  (pome/add-dependencies
   :coordinates  the-deps
   :repositories (merge cemerick.pomegranate.aether/maven-central
                        {"clojars" "http://clojars.org/repo"})))


(defn show-help
  []
  (println "
Usage:
    lein exec -e|-ep <string-s-expr>
    lein exec [-p] <script-path> [args]

-e  evaluates the following string as an s-expression
-ep evaluates the following string as an S-expression in project (w/classpath)
-p  indicates the script should be evaluated in project (with classpath)

Examples:
    lein exec -e '(println \"foo\" (+ 20 30)'
    lein exec -ep \"(use 'foo.bar) (pprint (map baz (range 200)))\"
    lein exec -p script/run-server.clj -p 8088
    lein exec ~/common/delete-logs.clj

Optional args after script-path are bound to clojure.core/*command-line-args*
"))


(defn eval-sexp
  "Evaluate S-expression"
  [project sexp-str]
  (if project
    ;; eval in project
    (eval/eval-in-project project `(load-string ~sexp-str))
    ;; else eval without project
    (load-string sexp-str))
  (flush)
  0)


(defn eval-script
  "Evaluate script. If project is not nil, evaluate the script in context of
  project (classpath)."
  [project script-path script-argstr]
  (if project
    ;; eval-in-project
    (eval/eval-in-project
     project
     `(binding [*command-line-args* (read-string ~script-argstr)]
        (load-file ~script-path)))
    ;; else external, eval without project
    (binding [*command-line-args* (read-string script-argstr)]
      (load-file script-path)))
  (flush)
  0)


(defn not-in-project
  []
  (binding [*err* *out*]
    (println "ERROR: Not in a project"))
  1)


(defn ^:no-project-needed exec
  "Executes a Clojure s-expr/script"
  [project & args]
  (let [option  (when (= \- (first (first args))) (first args))
        opt?    (fn [choice & more] (some #(= option %) (cons choice more)))
        params  (if option (rest args) args)]
    (if (empty? params)
      (show-help)
      (cond (opt? "-e")  (eval-sexp nil (first params))
            (opt? "-ep") (if project (eval-sexp project (first params))
                             (not-in-project))
            (opt? "-pe") (if project (eval-sexp project (first params))
                             (not-in-project))
            (opt? "-p")  (if project (eval-script project (first params)
                                                  (pr-str params))
                             (not-in-project))
            (not option) (eval-script nil (first params) (pr-str params))
            :otherwise   (show-help)))))