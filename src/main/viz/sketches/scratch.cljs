(ns viz.sketches.scratch
  (:require [viz.maths :refer [scale]]
            [viz.midi :refer [s]]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]))


(defn create-webgl [p]
  (.createCanvas p (.-windowWidth p) (.-windowHeight p) (.-WEBGL p)))

(defn create-2d [p]
  (.createCanvas p (.-windowWidth p) (.-windowHeight p)))

(def sketch1
  {:setup
   (fn [p]
     (create-webgl p))
   :draw
     (fn [p]
         (.background p 200)
         (.rotateY p (s :slider2 -1 1))
         (.rotateX p (s :slider3 -1 1))
         (.box p 200))})

;; box has angle
;; x / y derived from angle
;; box array will just be array of angles for now

;; https://www.mathopenref.com/coordcirclealgorithm.html
;;Parametric equation of circle
;; x = h + r * cos(a)
;; y = k + r * sin(a)
;; h,k = x,y of circle center

(defn box-ring [num]
  (map #(* % (/ 360 num)) (range 0 (inc num))))

;;Getting some weirdness with the coordinates with this one.
;; Weirdness is coming from canvas not being the right size

;; Rotate expects everything in radians
;; If I end up doing a load of calculations it may make more sense to use radians instead of converting every frame
;; deg / 180 * pi

;; This ones really harsh, might look better with some easing / not resetting the length every frame
;; Can have old value / new value
;; every 10 frames old-v = newv newv = (gen new vals)
;; Think about how to extract easing logic into afunction you can reuse
(defn deg->rad [v]
  (* (/ v 180) Math/PI))
(def sketch2
  {:setup
   (fn [p]
     (create-webgl p))

   :draw
   (fn [p]
     (let [h  0
           k  0
           r 150]
       (.background p 250)
       (.fill p 0)
       (.orbitControl p)
       (.rotateX p (s :slider3 -2 2))
       (.rotateY p (s :slider4 -2 2))
       (doseq [a (box-ring 50)]
         (let [x (+ h (* r (Math/cos a)))
               y (+ k (* r (Math/sin a)))]
           (.push p)
           (.translate p x y 0)
           (.rotateY p (deg->rad 90))
           (.box p (+ 50 (rand-int (s :slider2 100 800)))10)
           (.pop p)))
       ))})

;; Wanted to create sketch with vertices I can manipulate. Need to find a way of extracting the vertices that p5 uses to render an obj file.

;; 3x3 screen with spacing
;; dimensions should be parameterized
;; first display one image on each screen
;; Then work at splitting an image across them LED panel style.

(defn gen-wall [width height size gap]
  (map (fn [y-offset]
         (map (fn [x-offset]
                {:x (* x-offset (+ size gap)) :y (* y-offset (+ size gap))})
              (range width)))
       (range height)))

(gen-wall 3 3 50 10)

(def sketch3
  (let [skull (atom ())]
    {:preload
     (fn [p]
       (reset! skull (.loadImage p "./assets/skull.png")))
     :setup
     (fn [p]
       (create-webgl p))

     :draw
     (fn [p]
       (let [width 8
             height 8
             size 40
             gap 10]
         (.background p 0)
         (.orbitControl p)
         (doseq [r (gen-wall width height size gap)]
           (doseq [b r]
             (let [x (:x b) y (:y b)]
               (.push p)
               (.translate p x y 0)
               (.texture p @skull)
               (.box p size)
               (.pop p))))))}))

(defn gen-square [size]
  (let [half (/ size 2)]
    [{:x (- half) :y (- half) :n1 0 :n2 0}
     {:x half :y (- half) :n1 1 :n2 0}
     {:x half :y half :n1 1 :n2 1}
     {:x (- half) :y half :n1 0 :n2 1}
     ]))

;; (def sketch4
;;   (let [skull (atom false)]
;;     {:preload
;;      (fn [p]
;;        (js/console.log "hello preload")
;;        (reset! skull (.loadImage p "./assets/skull.png"))
;;        )
;;      :draw
;;      (fn [p]
;;        (.background p 200)
;;        (.fill p 0)
;;        (.texture p @skull)
;;        (.beginShape p)
;;        (doseq [v (gen-square 400)]
;;          (.vertex p (:x v) (:y v) 0 (:n1 v) (:n2 v)))
;;        (.endShape p (.-CLOSE p)))
;;      }))

(defn gen-rand-square [size v]
  (let [half (/ size 2)]
    [{:x  (+ (- (rand-int (* 2 v)) v) (- half))
      :y (+ (- (rand-int (* 2 v)) v) (- half))
      :n1 0 :n2 0}
     {:x  (+ (- (rand-int (* 2 v)) v)  half)
      :y (+ (- (rand-int (* 2 v)) v) (- half))
      :n1 1 :n2 0}
     {:x (+ (- (rand-int (* 2 v)) v) half)
      :y (+ (- (rand-int (* 2 v)) v) half)
      :n1 1 :n2 1}
     {:x (+ (- (rand-int (* 2 v)) v) (- half))
      :y (+ (- (rand-int (* 2 v)) v) half)
      :n1 0 :n2 1}
     ]))

(def sketch4
  (let [skull (atom false)]
    {:preload
     (fn [p]
       (let [loaded-skull (.loadImage p "./assets/skull.png")]
         (reset! skull loaded-skull)))
     :setup
     (fn [p]
       (create-webgl p)
       (.textureMode p (.-NORMAL p)))
     :draw
     (fn [p]
       (.background p 200)
       (.texture p @skull)
       (.beginShape p)
       (doseq [v (gen-square 400)]
         (.vertex p (:x v) (:y v) 0 (:n1 v) (:n2 v)))
       (.endShape p (.-CLOSE p))
       )
     }))

(def sketch5
  (let [skull (atom false)
        sq (gen-rand-square 400 100)]
    {:preload
     (fn [p]
       (let [loaded-skull (.loadImage p "./assets/skull.png")]
         (reset! skull loaded-skull)))
     :setup
     (fn [p]
       (create-webgl p)
       (.textureMode p (.-NORMAL p)))
     :draw
     (fn [p]
       (.background p 200)
       (.texture p @skull)
       (.beginShape p)
       (doseq [v (gen-rand-square 600 200)]
         (.vertex p (:x v) (:y v) 0 (:n1 v) (:n2 v)))
       (.endShape p (.-CLOSE p))
       )
     }))

(defn calculate-easing [v t e]
  (* (- t v) e))

(defn ease-square [v t e]
  {:x (+ (:x v) (calculate-easing (:x v) (:x t) e))
   :y (+ (:y v) (calculate-easing (:y v) (:y t) e))
   :n1 (:n1 v)
   :n2 (:n2 v)})

(map #(ease-square %1 %2 0.5) (gen-rand-square 400 100) (gen-rand-square 400 100))

(def sketch6
  (let [skull (atom false)
        sq (atom (gen-rand-square 400 800))
        new-sq (atom (gen-rand-square 400 800))
        easing 0.15]
    {:preload
     (fn [p]
       (let [loaded-skull (.loadImage p "./assets/full.png")]
         (reset! skull loaded-skull)))
     :setup
     (fn [p]
       (create-webgl p)
       (.textureMode p (.-NORMAL p)))
     :draw
     (fn [p]
       (.background p 255)
       (.texture p @skull)
       (.beginShape p)
       (doseq [v @sq]
         (.vertex p (:x v) (:y v) 0 (:n1 v) (:n2 v)))
       (.endShape p (.-CLOSE p))
       (reset! sq (map #(ease-square %1 %2 easing) @sq @new-sq))
       (if (= 0 (mod (.-frameCount p) 10))
         (reset! new-sq (gen-rand-square 400 800)))
       )
     }))

(def sketch7
  (let [full (atom false)
        eyes (atom false)
        mouth (atom false)]
    {:preload
     (fn [p]
       (let [l-full (.loadImage p "./assets/full.png")
             l-eyes (.loadImage p "./assets/eyes.png")
             l-mouth (.loadImage p "./assets/mouth.png")]
         (reset! full l-full)
         (reset! eyes l-eyes)
         (reset! mouth l-mouth)))
     :setup
     (fn [p]
       (create-webgl p))

     :draw
     (fn [p]
       (.background p 255)
       (.texture p @full)
       ;; (if (= 0 (mod (.-frameCount p) 30))
       ;;   (if (> 0.8 (rand))
       ;;     (let [pics [@eyes @mouth]]
       ;;       (.texture p (rand-nth pics)))))
       (.noStroke p)
       (.plane p 800)
)}))

(defn gen-vera-sq [max-sq-size min-sq-size sq-dec max-offset]
  ;; :x :y size
  (map (fn [size]
         {:x (- (rand-int (* 2 max-offset))
                   max-offset)
          :y (- (rand-int (* 2 max-offset))
                   max-offset)
          :size size})
       (reverse (range min-sq-size max-sq-size sq-dec))))
(gen-vera-sq 500 5 50 30)

(def sketch8
  (let [max-sq-size 900
        min-sq-size 30
        ;; sq-dec 100
        min-offset 20
        weight 1]
    {:setup
     (fn [p]
       (create-webgl p)
       (.noFill p)
       (.strokeWeight p 8))
     :draw
     (fn [p]
       (let [max-offset (s :slider3 100 500)
             sq-dec (s :slider4 5 400)]
         (if (= 0 (mod (.-frameCount p) 7))
           (do
             (.background p 255)
             (doseq [s (gen-vera-sq max-sq-size min-sq-size sq-dec max-offset)]
               (.push p)
               (.translate p (:x s) (:y s))
               (.plane p (:size s) )
               (.pop p))
             ))))}
    ;;)
  ))

(def sketch9
  (let [g (atom 255)
        s (atom 100)
        c-x (atom 0)
        c-y (atom 0)
        circle (atom false)]

    {:preload
     (fn [p]
       (let [circle-l (.loadImage p "./assets/circle-inv.png")]
         (reset! circle circle-l)
         ))
     :setup
     (fn [p]
       (create-webgl p)
       (.frameRate p 30)
       (.background p 0))
     :draw
     (fn [p]
       (let [g-dec 0.8
             s-inc 0.6
             x-max 100
             y-max 600]
         ;; (.noStroke p)
         ;; (.texture p @circle)
         ;; (.plane p 600)
         (.rotateX p 1.5)
         (.noStroke p)
         (.background p 0)
         (.fill p 255 255 255 @g)
         ;; (.translate p @c-x @c-y 0) ;; THis doesnt seem to be translating on y axis
         (.cylinder p @s 1) ;; could be too cpu intensive?
         ;; Is there a way to do this more functionally? with a seq?
         (swap! g - g-dec)
         (swap! s + s-inc)

         ;; (js/console.log @g)
         (if (<= @g 25)
           (do (reset! g 255)
               ;; (js/console.log @c-x @c-y)
               ;; (reset! c-x (- (rand-int (* 2 x-max)) x-max))
               ;; (reset! c-y (- (rand-int (* 2 y-max)) y-max))
               ;; (js/console.log @c-y)
               (reset! s 1)))))}))

;; (def s-partial-observer
;;   (let [heart (atom false)]
;;     {:preload
;;     (fn [p]
;;       (let [heart-l (.loadImage p "./assets/circle.png")]
;;         (reset! heart heart-l)))
;;     :setup
;;     (fn [p]
;;       (create-2d p)
;;       )
;;     :draw
;;      (fn [p]
;;        (if (= 0 (mod (.-frameCount p) 60))
;;          (do
;;            (.background p 255)
;;            (dotimes [n 30]
;;              (.image p @heart
;;                      (- (rand-int (.-width p)) 150)
;;                      (- (rand-int (.-height p)) 150)
;;                      300 300)))))
;;      }))

(def s-partial-observer-lines
  (let [heart (atom false)]
    {:preload
     (fn [p]
      (let [heart-l (.loadImage p "./assets/circle.png")]
        (reset! heart heart-l)))
     :setup
     (fn [p]
       (create-2d p)

      )
    :draw
     (fn [p]
       (let [width (.-width p)
             height (.-height p)]
         (.strokeWeight p 10)
         (if (= 0 (mod (.-frameCount p) 300))
           (.background p 255))
         (if (= 0 (mod (.-frameCount p) 6))

           (.line p
                  (rand-int width)
                  (rand-int height)
                  (rand-int width)
                  (rand-int height)))))
     }))

(def s-partial-observer-lines-thick
  (let [heart (atom false)]
    {:preload
     (fn [p]
      (let [heart-l (.loadImage p "./assets/circle.png")]
        (reset! heart heart-l)))
     :setup
     (fn [p]
       (create-2d p)

      )
    :draw
     (fn [p]
       (let [width (.-width p)
             height (.-height p)]
         (.strokeWeight p 25)
         (if (= 0 (mod (.-frameCount p) 800))
           (.background p 255))
         (if (= 0 (mod (.-frameCount p) 6))

           (.line p
                  (rand-int width)
                  (rand-int height)
                  (rand-int width)
                  (rand-int height)))))
     }))

(def s-partial-observer-head
  (let [head (atom false)]
    {
     :preload
     (fn [p]
      (let [head-l (.loadImage p "./assets/full.png")]
        (reset! head head-l)
        (js/console.log "preload")))
     :setup
     (fn [p]
       (create-2d p)
       (js/console.log "setup"))
     :draw
     (fn [p]
       (let [width (.-width p)
             height (.-height p)]
         (.strokeWeight p 5)
         (if (= 0 (mod (.-frameCount p) 600))
           (.background p 255))
         ;; (if (= 0 (mod (.-frameCount p) 6))
         ;;   (.image p @head 100 100 100 100))
         ))
     }))
(defn rand-between [min max]
  (+ min (rand-int (- max min))))

(defn gen-rand-spheres [n max-x max-y min-size max-size]
  (map (fn [i]
         (let [z-max 1000]
           {:x (- (rand-int (* 2 max-x)) max-x)
            :y (- (rand-int (* 2 max-y)) max-y)
            :z (- (rand-int (* 2 z-max)) z-max)
            :x-r (rand-between 200 800)
            :y-r (rand-between 200 800)
            :z-r (rand-between 200 800)
            :size (+ min-size (rand-int (- max-size min-size)))}))
       (range n)))

(gen-rand-spheres 5 10 10 10 100)
(def feel-so
  (let [spheres (atom false)]
    {
     :preload
     (fn [p] ())
     :setup
     (fn [p]
       (create-webgl p)
       (reset! spheres (gen-rand-spheres 200 1000 800 20 100)))
     :draw
     (fn [p]
       (.background p 0)
       (.rotateY p (/ (.millis p) 12000))
       (.normalMaterial p)
       (.noStroke p)
       (doseq [sp @spheres]
         (.push p)
         (.translate p (:x sp) (:y sp) (:z sp))
         (.rotateX p (/ (.millis p) (:x-r sp)))
         (.rotateY p (/ (.millis p) (:y-r sp)))
         (.rotateZ p (/ (.millis p) (:z-r sp)))
         (.sphere p (:size sp))
         (.pop p))
       )}))

(def feel-so-fast
  (let [spheres (atom false)]
    {
     :preload
     (fn [p] ())
     :setup
     (fn [p]
       (create-webgl p)
       (reset! spheres (gen-rand-spheres 200 1000 800 20 100)))
     :draw
     (fn [p]
       (.background p 0)
       (.rotateY p (/ (.millis p) 500))
       (.normalMaterial p)
       (.noStroke p)
       (doseq [sp @spheres]
         (.push p)
         (.translate p (:x sp) (:y sp) (:z sp))
         (.rotateX p (/ (.millis p) (:x-r sp)))
         (.rotateY p (/ (.millis p) (:y-r sp)))
         (.rotateZ p (/ (.millis p) (:z-r sp)))
         (.sphere p (:size sp))
         (.pop p))
       )}))

(def feel-so-fast-multi-rot
  (let [spheres (atom false)
        x-rot (rand-between 200 600)
        y-rot (rand-between 200 600)
        z-rot (rand-between 200 600)]
    {
     :preload
     (fn [p] ())
     :setup
     (fn [p]
       (create-webgl p)
       (reset! spheres (gen-rand-spheres 200 1000 800 20 100)))
     :draw
     (fn [p]
       (.background p 0)
       (.rotateY p (/ (.millis p) y-rot))
       (.rotateZ p (/ (.millis p) z-rot))
       (.rotateX p (/ (.millis p) x-rot))
       (.normalMaterial p)
       (.noStroke p)
       (doseq [sp @spheres]
         (.push p)
         (.translate p (:x sp) (:y sp) (:z sp))
         (.rotateX p (/ (.millis p) (:x-r sp)))
         (.rotateY p (/ (.millis p) (:y-r sp)))
         (.rotateZ p (/ (.millis p) (:z-r sp)))
         (.sphere p (:size sp))
         (.pop p))
       )}))


(defn gen-faces [num size-max x-max y-max rot-min rot-max]
  (map (fn [i] {:size (rand-between 100 size-max)
                :x (- (rand-between 0 x-max) (/ x-max 2))
                :y (- (rand-between 0 y-max) (/ y-max 2))
                :z (rand-between -200 200)
                :rot (rand-between  rot-min rot-max)})
       (range num)))


(def its-better-face
  (let [face (atom false)]
    {:preload
     (fn [p]
       (reset! face (.loadImage p "./assets/full.png")))
     :setup
     (fn [p]
       (create-webgl p)
       (.noStroke p)
       ;; (.fill p 0 0 0 0)
       (.noFill p)
       (.texture p @face))
     :draw
     (fn [p]
       (.plane p 800))
     }))
(def nu-year-many
  (let [face (atom false)
        faces (atom false)]
    {:preload
     (fn [p]
       (reset! face (.loadImage p "./assets/full.png")))
     :setup
     (fn [p]
       (create-webgl p)
       (.noStroke p)
       ;; (.fill p 0 0 0 0)
       (reset! faces (gen-faces 50 300 1500 1500 500 1200))
       (.noFill p)
       (.texture p @face))
     :draw
     (fn [p]
       (.background p 255)
       (doseq [f @faces]
         (.push p)
         (.translate p (:x f) (:y f) (:z f))
         (.rotateY p (/ (.millis p) (:rot f)))
         (.plane p (:size f))
         (.pop p))
       )
     }))

(def nu-year-many-fast
  (let [face (atom false)
        faces (atom false)]
    {:preload
     (fn [p]
       (reset! face (.loadImage p "./assets/full.png")))
     :setup
     (fn [p]
       (create-webgl p)
       (.noStroke p)
       ;; (.fill p 0 0 0 0)
       (reset! faces (gen-faces 100 800 1000 1500 500 800))
       (.noFill p)
       (.texture p @face))
     :draw
     (fn [p]
       (.background p 255)
       (doseq [f @faces]
         (.push p)
         (.translate p (:x f) (:y f) (:z f))
         (.rotateY p (/ (.millis p) (:rot f)))
         (.plane p (:size f))
         (.pop p))
       )
     }))
