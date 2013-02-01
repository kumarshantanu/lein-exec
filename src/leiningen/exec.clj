(ns leiningen.exec
  (:require [leiningen.core.eval  :as eval]
            [leiningen.core.main  :as main]
            [cemerick.pomegranate :as pome]))


(defn deps
  "Pull `the-deps` dependencies from Maven Central and Clojars. This is a
  convenience function for scripts executed by this plugin.
  Example:
    (use '[leiningen.exec :only (deps)])
    (deps '[[compojure \"1.0.1\"]
            [org.clojure/java.jdbc \"0.1.0\"]]
          :repositories {\"jboss\" \"https://repository.jboss.org/nexus/content/repositories/\"})"
  [the-deps & {:keys [repositories]}]
  (pome/add-dependencies
    :coordinates  the-deps
    :repositories (merge cemerick.pomegranate.aether/maven-central
                         {"clojars" "http://clojars.org/repo"}
                         repositories)))

(defn show-help
  []
  (println "
Usage:
    lein exec [-p]
    lein exec -e[p] <string-s-expr>
    lein exec [-p] <script-path> [args]

When invoked without args it reads S-expressions from STDIN and evaluates them.
When only option `-p` is specified, it evaluates STDIN in project context.

-e  evaluates the following string as an S-expression
-ep evaluates the following string as an S-expression in project (w/classpath)
-p  indicates the script should be evaluated in project (with classpath)

Examples:
    cat foo.clj | lein exec
    lein exec -e '(println \"foo\" (+ 20 30)'
    lein exec -ep \"(use 'foo.bar) (pprint (map baz (range 200)))\"
    lein exec -p script/run-server.clj -p 8088
    lein exec ~/common/delete-logs.clj

Optional args after script-path are bound to clojure.core/*command-line-args*
Executable Clojure script files should have the following on the first line:
#!/usr/bin/env lein exec
"))


(defn eval-stdin
  "Evaluate STDIN. If project is not nil, evaluate in context of project."
  [project]
  (if project
    ;; eval in project
    (eval/eval-in-project project `(load-reader *in*))
    ;; else eval without project
    (load-reader *in*))
  (flush)
  (main/exit 0))


(defn eval-sexp
  "Evaluate S-expression. If project is not nil, evaluate in context of project."
  [project sexp-str]
  (if project
    ;; eval in project
    (eval/eval-in-project project `(load-string ~sexp-str))
    ;; else eval without project
    (load-string sexp-str))
  (flush)
  (main/exit 0))


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
  (main/exit 0))


(defmacro in
  "Execute body of code in context of project"
  [project & body]
  `(if ~project (do ~@body)
       (do (binding [*err* *out*]
             (println "ERROR: Not in a project"))
           1)))


(defn ^:no-project-needed exec
  "Execute Clojure S-expresions from command-line or scripts"
  [project & args]
  (let [option  (when (= \- (first (first args))) (first args))
        opt?    (fn [choice & more] (some #(= option %) (cons choice more)))
        params  (if option (rest args) args)]
    (cond ;; eval STDIN
          (empty? params) (cond (nil? option) (eval-stdin nil)
                                (opt? "-p")   (in project (eval-stdin project))
                                :otherwise    (show-help))
          ;; eval (first param) from command line
          (opt? "-e")   (eval-sexp nil (first params))
          (opt? "-ep")  (in project (eval-sexp project (first params)))
          (opt? "-pe")  (in project (eval-sexp project (first params)))
          ;; eval script
          (opt? "-p")   (in project (eval-script project (first params)
                                                 (pr-str params)))
          (not option)  (eval-script nil (first params) (pr-str params))
          :otherwise    (show-help))))
