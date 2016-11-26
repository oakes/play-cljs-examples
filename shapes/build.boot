(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[adzerk/boot-cljs "1.7.228-1" :scope "test"]
                  [adzerk/boot-reload "0.4.12" :scope "test"]
                  [pandeiro/boot-http "0.7.3" :scope "test"]
                  ; project deps
                  [nightlight "1.2.1"]
                  [org.clojure/clojurescript "1.9.225"]
                  [play-cljs "0.6.6"]])

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-reload :refer [reload]]
  '[pandeiro.boot-http :refer [serve]]
  '[nightlight.boot :refer [nightlight]])

(deftask run []
  (comp
    (serve :dir "target/public")
    (watch)
    (reload)
    (cljs :source-map true :optimizations :none)
    (target)
    (nightlight :port 4000 :url "http://localhost:3000")))

(deftask build []
  (comp (cljs :optimizations :simple) (target)))

