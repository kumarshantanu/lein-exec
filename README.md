# lein-exec

Leiningen plugin to execute Clojure scripts in a project


## Usage

Either install as a plugin:

    $ lein plugin install lein-exec "0.1"

Or, include as a dev-dependency:

    :dev-dependencies [[lein-exec "0.1"]]

You can execute scripts as follows:

    $ lein exec scripts/foobar.clj              # mention script path
    $ lein exec scripts/run-server.clj -p 4000  # with arguments

The script would have dependencies and source packages on CLASSPATH.
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

Copyright (C) 2011 Shantanu Kumar

Distributed under the Eclipse Public License, the same as Clojure.
