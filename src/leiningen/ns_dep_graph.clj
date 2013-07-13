(ns leiningen.ns-dep-graph
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.tools.namespace.file :as ns-file]
            [clojure.tools.namespace.track :as ns-track]
            [clojure.tools.namespace.find :as ns-find]
            [clojure.tools.namespace.dependency :as ns-dep]
            [rhizome.viz :as viz]))

(defn ns-dep-graph
  "Create a namespace dependency graph and save it as ns-dep-graph.png."
  [project & args]
  (let [source-files (apply set/union
                            (map (comp ns-find/find-clojure-sources-in-dir
                                       io/file)
                                 (project :source-paths)))
        tracker (ns-file/add-files {} source-files)
        dep-graph (tracker ::ns-track/deps)
        ns-names (set (map (comp second ns-file/read-file-ns-decl)
                           source-files))
        part-of-project? (partial contains? ns-names)
        nodes (filter part-of-project? (ns-dep/nodes dep-graph))]
    (viz/save-graph
     nodes
     #(filter part-of-project? (ns-dep/immediate-dependencies dep-graph %))
     :node->descriptor (fn [x] {:label x})
     :options {:dpi 72}
     :filename "ns-dep-graph.png")))

;; TODO: make output filename configurable.

;; TODO: do not overwrite existing PNG file.

;; TODO: maybe add option to show dependencies on external namespaces as well.
