(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[adzerk/boot-cljs "2.1.5" :scope "test"]
                  [adzerk/boot-reload "0.6.0" :scope "test"]
                  [pandeiro/boot-http "0.8.3" :scope "test"
                   :exclusions [org.clojure/clojure]]
                  [javax.xml.bind/jaxb-api "2.3.0"] ; necessary for Java 9 compatibility
                  ; project deps
                  [edna "1.6.0"]
                  [nightlight "RELEASE"]
                  [org.clojure/clojurescript "1.10.439"]
                  [play-cljs "1.3.0"]])

(require
  '[clojure.java.io :as io]
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-reload :refer [reload]]
  '[pandeiro.boot-http :refer [serve]]
  '[nightlight.boot :refer [nightlight]])

(deftask run []
  (comp
    (serve :dir "target/public" :port 3000)
    (watch)
    (reload)
    (cljs
      :optimizations :none
      :compiler-options {:asset-path "main.out"})
    (target)
    (nightlight :port 4000 :url "http://localhost:3000")))

(defn delete-children-recursively!
  "Deletes the children of the given dir along with the dir itself."
  [f]
  (when (.isDirectory f)
    (doseq [f2 (.listFiles f)]
      (delete-children-recursively! f2)))
  (when (.exists f) (io/delete-file f))
  nil)

(deftask build []
  (comp
    (cljs :optimizations :advanced)
    (target)
    (with-pass-thru _
      (.renameTo (io/file "target/public") (doto (io/file "../docs/demos/super-koalio")
                                             delete-children-recursively!)))))

