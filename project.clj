(defproject lein-ns-dep-graph "0.4.0"
  :description "Show namespace dependencies of project sources as a graph."
  :url "https://github.com/hilverd/lein-ns-dep-graph"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies [[org.clojure/tools.namespace "1.1.0"]
                 [rhizome "0.1.8"]])
