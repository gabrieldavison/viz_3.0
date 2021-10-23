(ns viz.app
  (:require [viz.macro :refer [h]]
            [viz.p5 :refer [init-p5 load-sketch]]
            [viz.sketches.scratch :refer [sketch1 sketch2 sketch3 sketch4 sketch5 sketch6]]
            [viz.midi :refer [start-midi s]]
            [viz.maths :refer [scale]]))

(defn init []
  (println "hello world"))

(def sketch (init-p5))

(start-midi)

(load-sketch sketch6)

(h (src js/s1)
   (modulate (h (osc #(s :slider1 0 100))))
   (out))
