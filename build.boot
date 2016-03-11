(set-env!
 :resource-paths #{"html"}
 :source-paths  #{"src"}
 :dependencies '[[adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                 [adzerk/boot-cljs-repl       "0.3.0"          :scope "test"]
                 [adzerk/boot-reload          "0.4.5"          :scope "test"]
                 [pandeiro/boot-http          "0.7.1-SNAPSHOT" :scope "test"]
                 [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]

                 [org.clojure/clojure         "1.7.0"]
                 [org.clojure/clojurescript   "1.7.228"]
                 [reagent                     "0.5.1"]
                 [com.cemerick/piggieback     "0.2.1"          :scope "test"]
                 [weasel                      "0.7.0"          :scope "test"]
                 [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]])

(require
  '[adzerk.boot-cljs      :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload    :refer [reload]]
  '[crisptrutski.boot-cljs-test  :refer [test-cljs]]
  '[pandeiro.boot-http    :refer [serve]])

(defn delete-recursively [fname]
  (let [func (fn [func f]
               (when (.isDirectory f)
                 (doseq [f2 (.listFiles f)]
                   (func func f2)))
               (clojure.java.io/delete-file f))]
    (func func (clojure.java.io/file fname))))

(deftask dev []
  (comp (serve)
        (watch)
        (reload :on-jsload 'playfair-cipher.core/main)
        (cljs-repl)
        (cljs :source-map true :optimizations :none)))


(deftask release []
  (comp
   (cljs :optimizations :advanced)
   (target)))

(deftask testing []
  (merge-env! :source-paths #{"test"})
  identity)


(deftask auto-test []
  (comp (testing)
        (watch)
        (test-cljs)
        (testing)))
