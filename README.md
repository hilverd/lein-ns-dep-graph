[![Clojars Project](https://img.shields.io/clojars/v/lein-ns-dep-graph.svg)](https://clojars.org/lein-ns-dep-graph)

# lein-ns-dep-graph

This is a Leiningen plugin to show the namespace dependencies of Clojure project
sources as a graph.

## Acknowledgements

The plugin itself is tiny, all the hard work is done by
[clojure.tools.namespace](https://github.com/clojure/tools.namespace) and
[Rhizome](https://github.com/ztellman/rhizome).

## Requirements

You will need to have [Graphviz](http://www.graphviz.org/) installed. Run `dot
-V` at the command line to check.

## Installation and Usage

Put `[lein-ns-dep-graph "0.4.0"]` into the `:plugins` vector of your
`:user` profile, or if you are on Leiningen 1.x do `lein plugin install
lein-ns-dep-graph 0.4.0`. Then run

```sh
lein ns-dep-graph
```

from a Clojure project directory. This outputs a file `ns-dep-graph.png` showing
the internal namespace dependencies of the project's `.clj` sources.
Dependencies on external namespaces, say `clojure.java.io`, are not shown.

### Command-Line Options
#### `-platform`
Specify `:cljs` as the platform argument to generate a graph for ClojureScript:

```sh
lein ns-dep-graph -platform :cljs # or
lein ns-dep-graph -platform :clj # (default)
```

#### `-parents`
Specify `-parents` to generate a graph showing only the nodes that have (one of)
those namespaces as their transitive parent:

```sh
lein ns-dep-graph -parents [my-app.module.b.core]
```

#### `-name`
Output to the file `NAME.png` instead of `ns-dep-graph.png`.

#### `-format`
A clojure form representing a unary function that will be applied to each namespace label.
For example:
```sh
; Remove project prefix from dep labels
lein ns-dep-graph -format '(fn [label] (.replace label "com.example-project." ""))'
```

## Examples

Below is the namespace dependency graph obtained for
[Hiccup](https://github.com/weavejester/hiccup).

![Hiccup namespace dependency graph](http://hilverd.github.com/lein-ns-dep-graph/img/hiccup.png)

## License

Copyright © 2021 Hilverd Reker.

Distributed under the Eclipse Public License, the same as Clojure.
