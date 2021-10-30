(ns viz.app
  (:require [viz.macro :refer [h]]
            [viz.p5 :refer [init-p5 init-sketch]]
            ;; [viz.sketches.scratch :refer [sketch1 sketch2 sketch3 sketch4 sketch5 sketch6 sketch7 sketch8 sketch9 s-partial-observer-lines s-partial-observer-head]]
            [viz.sketches.scratch :as sk]
            [viz.midi :refer [start-midi s clear-buttons]]
            [viz.maths :refer [scale]]
            [viz.kb :refer [setup-kb set-k]]))

(defn init []
  (println "hello world"))
(setup-kb)

(def s1 js/s1)
(def o0 js/o0)
(def o1 js/o1)
(def o2 js/o2)
(def o3 js/o3)

;; (def sketch (init-p5))

(start-midi)


(defn ooh-ah []
  (clear-buttons)
  (init-sketch sk/sketch7)
  (set-k "q" (fn [] (h (src s1)
                          (modulate (h (osc #(s :slider1 0 100))))
                          (blend (h (src o1) (diff o0)) 0.1)
                          (modulate (h (noise 300)) #(s :slider2 0.01 0.1))
                          (invert)
                          (out))))

  (set-k "w" (fn [] (h (src s1)
                          (modulate (h (osc #(s :slider1 0 100))))
                          (blend (h (src o1) (diff o0)) 0.1)
                          (modulate (h (noise 300)) #(s :slider2 0 0.1))
                          (diff (h (src o0) (modulateScale (h (noise 10) (pixelate 40 40))) (invert)) 0.2)
                          (modulate (h (noise 200) (pixelate 20 20)) #(s :slider3 0 0.8))
                          (out))))

  (set-k "e" (fn [] (h (src s1)
                          (modulate (h (osc #(s :slider1 0 100))))
                          (blend (h (src o1) (diff o0)) 0.1)
                          (modulate (h (noise 300)) #(s :slider2 0 0.1))
                          (invert)
                          (diff (h (osc 50 0.9) (pixelate 1 1)))
                          (out))))
  (set-k "r" (fn [] (h (src s1)
                          (modulate (h (osc #(s :slider1 0 100))))
                          (blend (h (src o1) (diff o0)) 0.1)
                          ;; (modulate (h (noise 300)) #(s :slider2 0 0.1))
                          (invert)
                          (diff (h (osc 50 0.9) (pixelate 1 1)))
                          (modulate (h (noise 200) (pixelate 20 20)) #(s :slider3 0 0.8))
                          (out)))))

(defn disaster-pop []
  (clear-buttons)
  (init-sketch sk/sketch8)
  (set-k "q" (fn []
                  (h (src s1)
                     (blend (h (src o0)) 0.5)
                     ;; (modulate (h(voronoi 1000)) #(s :slider2 0 0.05))
                     (modulate (h (noise 50) (pixelate #(s :slider2 5 100)
                                                       #(s :slider2 5 100))) #(s :slider1 0 0.9))
                     (contrast 3)
                     (out))))
  (set-k "w" (fn []
                  (h (src s1)
                     (blend (h (src o0)) 0.5)
                     (modulate (h(voronoi 1000)) 0.02)
                     (modulate (h (noise 50) (pixelate 5 5)) #(s :slider1 0 0.9))
                     (add (h (noise 2) (pixelate 50 50) (contrast 0.5)))
                     (contrast 3)
                     (invert)
                     (out)))))

(defn glassbox[]
  (clear-buttons)
  (init-sketch sk/sketch9)
  (set-k "q" (fn []
                  (h (src s1)
                     (modulate (h (osc 100)) #(s :slider1 0.01 0.2))
                     (scale 2.4)
                     (pixelate 200 200)
                     (modulate (h (osc 1000) (kaleid 8)) #(s :slider2 0.03 0.2))
                     (modulate (h (noise 200)
                                  (pixelate (.fast (array 4 8 10) 0.2) (.fast (array 3 7 10) 0.7)))
                               #(s :slider3 0 0.2))
                     (contrast 2)
                     (out))))
  (set-k "w" (fn []
                  (h (src s1)
                     (modulate (h (osc 100)) #(s :slider1 0.01 0.2))
                     (pixelate 200 200)
                     (modulate (h (osc 1000) (kaleid 8)) #(s :slider2 0.03 0.2))
                     (contrast 2)
                     (scale 2.1)
                     (kaleid #(s :slider3 5 8))
                     (add (h(src o1)))
                     (out))))
  (set-k "e" (fn []
                  (h (src s1)
                     (modulate (h (osc 100)) #(s :slider1 0.01 0.2))
                     (pixelate 200 200)
                     (modulate (h (osc 1000) (kaleid 8)) #(s :slider2 0.03 0.2))
                     (contrast 2)
                     ;; (invert)
                     (scale 2.1)
                     (kaleid #(s :slider3 3 8))
                     (modulate (h (noise 2) (pixelate 50 50)))
                     (contrast 2)
                     (out)))))

(defn partial-observer []
  (clear-buttons)
  (init-sketch sk/s-partial-observer-lines)
  (set-k "q" (fn []
                  (init-sketch sk/s-partial-observer-lines)
                  (h (src s1)
                     (blend (h (src o0)) 0.95)
                     (modulate (h (noise 5)) #(s :slider1 0 0.05))
                     (modulate (h (osc 2 0.01))0.001)
                     (out))
                  ))
  (set-k "w" (fn []
                  (init-sketch sk/s-partial-observer-lines)
                  (h (src s1)
                     (kaleid 2)
                     (blend (h (src o0)) 0.95)
                     (modulate (h (noise 5)) #(s :slider1 0 0.05))
                     (modulate (h (osc 2 0.01))0.001)
                     (out))
                  ))
  (set-k "e" (fn []
                  (init-sketch sk/s-partial-observer-head)
                  (h (src s1)
                     (blend (h (src o0)) 0.95)
                     (modulate (h (noise 5)) #(s :slider1 0 0.05))
                     (modulate (h (osc 2 0.01))0.001)
                     (out))
                  )))

;; May need to adjust now that im running everyhting in a higher resolution
(defn feel-so []
  (clear-buttons)
  (set-k "q" (fn []
                  (init-sketch sk/feel-so)
                  (h
                   (src s1)
                   (modulate (h (osc 20000)) #(s :slider1 0 0.1))
                   (saturate 0)
                   (pixelate 800 800)
                   (contrast 2)
                   (out)
                   )))
  (set-k "w" (fn []
                  (init-sketch sk/feel-so-fast)
                  (h
                   (src s1)
                   (modulate (h (osc 20000)) #(s :slider1 0 0.1))
                   (saturate 0)
                   (pixelate 800 800)
                   (contrast 2)
                   (out)
                   )))
  (set-k "e" (fn []
                  (init-sketch sk/feel-so-fast-multi-rot)
                  (h
                   (src s1)
                   (modulate (h (osc 20000)) #(s :slider1 0 0.1))
                   ;; (diff (h(src o0) (pixelate 500 500)))
                   (saturate 0)
                   (pixelate 800 800)
                   (contrast 2)
                   (blend (h (osc 70 0.7) (pixelate 1 1)) #(s :slider2 0 1))
                   (out)
                   )))
  (set-k "r" (fn []
                  (init-sketch sk/feel-so-fast-multi-rot)
                  (h
                   (src s1)
                   (modulate (h (osc 20000)) #(s :slider1 0 0.1))
                   ;; (diff (h(src o0) (pixelate 500 500)))
                   (saturate 0)
                   (pixelate 800 800)
                   (contrast 2)
                   (blend (h (osc 100 0.7) (pixelate 1 1)) #(s :slider2 0 1))
                   (invert)
                   (out)
                   ))))

(defn nu-year []
  (clear-buttons)
  (set-k "q" (fn []
                  (init-sketch sk/nu-year-many)
                  (h (src s1)
                     ;; (modulate (h (src o0) (diff (h (noise 10)))) #(s :slider1 0 0.8))
                     (modulate (h (noise #(s :slider1 1 10))))
                     (saturate 0)
                     (pixelate 800 800)
                     (blend (h (src o0)) 0.8)
                     (blend (h (src o0)))
                     (blend (h (src o0)))
                     (contrast #(s :slider2 1 1.1))
                     (out))))
  (set-k "w" (fn []
                  (init-sketch sk/nu-year-many-fast)
                  (h (src s1)
                     ;; (modulate (h (src o0) (diff (h (noise 10)))) #(s :slider1 0 0.8))
                     (modulate (h (noise #(s :slider1 0.5 10))))
                     (saturate 0)
                     (pixelate 800 800)
                     (blend (h (src o0)) 0.8)
                     (blend (h (src o0)))
                     (contrast #(s :slider2 1 1.4))
                     (colorama #(s :slider3 0 0.2))
                     (saturate 0)
                     (out)))))


(defn its-better []
  (clear-buttons)
  (set-k "q" (fn []
                  (init-sketch sk/its-better-face)
                  (h (src s1)
                     (pixelate (.fast (array 20 50 100 200) 10)
                               (.fast (array 20 50 100 200) 10))
                     (modulate (h (noise 100) (pixelate 50 50)) #(s :slider1 0 0.2))
                     (contrast 5)
                     (out))))
  (set-k "w" (fn []
                  (init-sketch sk/its-better-face)
                  (h (osc 2000 0.3)
                     (modulate (h (osc 5 0.2)))
                     (layer (h (src s1)
                              (pixelate (.fast (array 20 50 100 200) 10)
                                        (.fast (array 20 50 100 200) 10)))0.8)
                     (modulate (h (noise 100) (pixelate 50 50)) #(s :slider1 0 0.2))
                     (contrast 5)
                     (out))))
  (set-k "e" (fn []
                  (init-sketch sk/its-better-face)
                  (h (osc 100 0.3)
                     (modulate (h (osc 1000 0.9)))
                     (layer (h (src s1)
                              (pixelate (.fast (array 20 50 100 200) 10)
                                        (.fast (array 20 50 100 200) 10)))0.8)
                     (modulate (h (noise 100) (pixelate 50 50)) #(s :slider1 0 0.2))
                     (contrast 5)
                     (out)))))

;; (defn template []
;;   (clear-buttons)
;;   (init-sketch sketch9)
;;   (set-k 0 (fn []
;;                   (h (src s1)
;;                      (out)))))

(set-k 1 #(disaster-pop))
;; (ooh-ah)
;; (glassbox)
;; (partial-observer)
;; (feel-so)
;; (nu-year)
;; (its-better)
