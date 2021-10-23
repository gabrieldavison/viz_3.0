(ns viz.sketches.scratch
  (:require [viz.maths :refer [scale]]
            [viz.midi :refer [s]]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(def sketch1
    {:draw
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
  {:draw
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
        easing 0.09]
    {:preload
     (fn [p]
       (let [loaded-skull (.loadImage p "./assets/skull.png")]
         (reset! skull loaded-skull)))
     :setup
     (fn [p]
       (.textureMode p (.-NORMAL p)))
     :draw
     (fn [p]
       (.background p 200)
       (.texture p @skull)
       (.beginShape p)
       (doseq [v @sq]
         (.vertex p (:x v) (:y v) 0 (:n1 v) (:n2 v)))
       (.endShape p (.-CLOSE p))
       (reset! sq (map #(ease-square %1 %2 easing) @sq @new-sq))
       (if (= 0 (mod (.-frameCount p) 5))
         (reset! new-sq (gen-rand-square 400 800)))
       )
     }))
