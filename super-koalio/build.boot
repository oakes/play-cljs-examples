(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[adzerk/boot-cljs "2.1.4" :scope "test"]
                  [adzerk/boot-reload "0.5.2" :scope "test"]
                  [pandeiro/boot-http "0.8.3" :scope "test"
                   :exclusions [org.clojure/clojure]]
                  ; project deps
                  [nightlight "2.0.6"]
                  [org.clojure/clojurescript "1.9.946"]
                  [play-cljs "0.11.2"]])

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-reload :refer [reload]]
  '[pandeiro.boot-http :refer [serve]]
  '[nightlight.boot :refer [nightlight]])

(deftask run []
  (comp
    (serve :dir "target/public" :port 3000)
    (watch)
    (reload)
    (cljs :source-map true :optimizations :none)
    (target)
    (nightlight :port 4000 :url "http://localhost:3000")))

(deftask build []
  (comp (cljs :optimizations :advanced) (target)))

