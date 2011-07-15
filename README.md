# lein-exec

Leiningen plugin to execute Clojure scripts


## Usage

Either install as a plugin:

    $ lein plugin install lein-exec "0.1"

Or, include as a dev-dependency:

    :dev-dependencies [lein-exec "0.1"]

You can execute a script as follows:

    $ lein exec scripts/foobar.clj    # just mention the script path

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
      (foo/bar :some-stuff)
      (baz ", "))


## License

Copyright (C) 2011 Shantanu Kumar

Distributed under the Eclipse Public License, the same as Clojure.
