(ns leiningen.ns-dep-graph
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.edn :as edn]
            [clojure.tools.namespace.file :as ns-file]
            [clojure.tools.namespace.track :as ns-track]
            [clojure.tools.namespace.find :as ns-find]
            [clojure.tools.namespace.dependency :as ns-dep]
            [rhizome.viz :as viz])
  (:import [java.io PushbackReader]))

(defn- add-image-extension [name]
  (str name ".png"))

(defn- hash-user-arguments [args options]
  (try (apply hash-map args)
  (catch Exception e (do (println "WARNING: Optional argument missing a corresponding value. Defaulting."))
                          options)))

(defn- build-arguments [args]
  (let [options {"-name"     "ns-dep-graph"
                 "-platform" ":clj"
                 "-parents"  "[]"
                 "-format" "clojure.core/identity"}
        hashed-args (hash-user-arguments args options)
        valid-options (remove nil? (map #(find hashed-args (first %)) options))]
    (merge options (into {} (filter (comp some? val) valid-options)))))

(defn ns-dep-graph
  "Create a namespace dependency graph and save it as either ns-dep-graph or the supplied name."
  [project & args]
  (let [built-args (build-arguments args)
        file-name (get built-args "-name")
        platform (case (edn/read-string (get built-args "-platform"))
                   :clj ns-find/clj
                   :cljs ns-find/cljs
                   ns-find/clj)
        format-fn (eval (read-string (get built-args "-format")))
        source-files (apply set/union
                            (map (comp #(ns-find/find-sources-in-dir % platform)
                                       io/file)
                                 (project :source-paths)))
        tracker (ns-file/add-files {} source-files)
        dep-graph (tracker ::ns-track/deps)
        ns-names (set (map (comp second ns-file/read-file-ns-decl)
                           source-files))
        part-of-project? (partial contains? ns-names)
        ns-parents (set (edn/read-string (get built-args "-parents")))
        part-of-parents?  #(or (empty? ns-parents)
                               (contains? ns-parents %)
                               (boolean (seq (set/intersection ns-parents (ns-dep/transitive-dependents dep-graph %)))))
        nodes (->> (ns-dep/nodes dep-graph)
                   (filter part-of-project?)
                   (filter part-of-parents?))]
      (loop [name file-name
             counter 1]
          (if (.exists (io/file (add-image-extension name)))
              (recur (str file-name counter) (inc counter))
              (viz/save-graph
               nodes
               #(filter part-of-project? (ns-dep/immediate-dependencies dep-graph %))
               :node->descriptor (fn [x] {:label (format-fn x)})
               :options {:dpi 72}
               :filename (add-image-extension name))))))

;; TODO: maybe add option to show dependencies on external namespaces as well.
