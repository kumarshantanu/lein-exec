# lein-exec

Leiningen plugin to execute Clojure scripts


## Installation

### Lein 2 users

The recommended way is to install as a global plugin in `~/.lein/profiles.clj`:

    {:user {:plugins [[lein-exec "0.2.0"]]}}

You may also install as a project plugin in `project.clj`:

    :plugins [[lein-exec "0.2.0"]]


### Lein 1.x users

Either install as a plugin:

    $ lein plugin install lein-exec "0.1"

Or, include as a dev-dependency:

    :dev-dependencies [[lein-exec "0.1"]]


## Usage

### Lein 2 users

This blog post covers lein-exec with examples:

[http://charsequence.blogspot.in/2012/04/scripting-clojure-with-leiningen-2.html]
(http://charsequence.blogspot.in/2012/04/scripting-clojure-with-leiningen-2.html)

#### Synopsis

    lein2 exec [-p]
    lein2 exec -e[p] <string-s-expr>
    lein2 exec [-p] <script-path> [args]

When invoked without args it reads S-expressions from STDIN and evaluates them.
When only option `-p` is specified, it evaluates STDIN in project context.

    -e  evaluates the following string as an S-expression
    -ep evaluates the following string as an S-expression in project (w/classpath)
    -p  indicates the script should be evaluated in project (with classpath)

#### Examples

    cat foo.clj | lein2 exec
    lein2 exec -e '(println "foo" (+ 20 30))'
    lein2 exec -ep "(use 'foo.bar) (pprint (map baz (range 200)))"
    lein2 exec -p script/run-server.clj -p 8088
    lein2 exec ~/common/delete-logs.clj

Optional args after script-path are bound to `clojure.core/*command-line-args*`

#### Getting dependencies from within script

Thanks to the *pomegranate* library in Leiningen, lein-exec exposes an API to
specify dependencies that can be added dynamically to the CLASSPATH:

    (use '[leiningen.exec :only (deps)])
    (deps '[[ring/ring-core "1.0.0"]
            [ring/ring-jetty-adapter "1.0.0"]])

This downloads dependencies from Maven Central and Clojars if required, and
uses the local repo on subsequent runs.

#### Executable scripts

*This may be applicable to Unix-like systems only.*

To run executable Clojure script files, you need to download the
[lein-exec](https://github.com/kumarshantanu/lein-exec/blob/master/lein-exec) and
[lein-exec-p](https://github.com/kumarshantanu/lein-exec/blob/master/lein-exec-p)
scripts, make them executable, and put them in PATH. After downloading the
scripts, you may also need to edit them in order to specify the correct name
for the Leiningen executable.

    $ wget https://github.com/kumarshantanu/lein-exec/blob/master/lein-exec
    $ wget https://github.com/kumarshantanu/lein-exec/blob/master/lein-exec-p
    $ chmod a+x lein-exec lein-exec-p
    $ mv lein-exec lein-exec-p ~/bin  # assuming ~/bin is in PATH

Executable Clojure script files that need not run *only* in project-scope
should have the following on the first line (shebang):

    #!/usr/bin/env lein-exec
or,

    #!/bin/bash lein-exec

Executable Clojure script files that are supposed to run *only* in project-scope
should have the following on the first line (shebang):

    #!/usr/bin/env lein-exec-p
or,

    #!/bin/bash lein-exec-p


### Lein 1.x users

You can execute scripts as follows in a project:

    $ lein exec scripts/foobar.clj              # mention script path
    $ lein exec scripts/run-server.clj -p 4000  # with arguments

The script would have project dependencies and source packages on CLASSPATH.


## Writing code to eval with lein-exec

It needs to be written as if would be eval'ed (rather than compiled) - example below:

    (require '[clojure.string :as str]
             '[clojure.pprint :as ppr]
             '[com.example.foo :as foo])
    
    
    (defn baz
      [y]
      (let [x (str/join y (foo/quux :bla-bla))]
        (ppr/pprint [x foo/nemesis])))
    
    (foo/bar :some-stuff)
    
    (do
      (println *command-line-args*)  ; command-line args as a list
      (foo/bar :some-stuff)
      (baz ", "))


## Getting in touch

On Twitter: [@kumarshantanu](http://twitter.com/kumarshantanu)

On Leiningen mailing list: [http://groups.google.com/group/leiningen](http://groups.google.com/group/leiningen)


## License

Copyright (C) 2011-2012 Shantanu Kumar

Distributed under the Eclipse Public License, the same as Clojure.
