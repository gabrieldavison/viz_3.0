(ns viz.p5
  (:require ["p5" :as p5]))


;; These atoms mean I can swap the draw/setup functions so I dont have to create a new sketch every time.
(def p5-preload (atom (fn [])))

(def p5-setup (atom (fn [])))

;; Sets p5 canvas as an input to hydra on js/s1
;; Would be good to set the canvas id to something specific just to be safe but at the moment that causes a bug

(defn p5-hydra-setup
  ([p]
   (.init js/s1 (clj->js {:src (.getElementById js/document "defaultCanvas0") }))))

(def p5-init-setup (fn [p]
                     (@p5-setup p)
                     (p5-hydra-setup p)
                     ))

(def p5-draw (atom (fn [])))


(def running-sketch (atom false))
;; Need to refactor this to destroy and create on load (would be good to still offer to swap draw method)
;; load-sketch has been refactored out for now

;; (defn load-sketch
;;   "Expects a map with :draw and (optionally) :setup fields"
;;   [s]
;;   (println s)
;;   (do
;;     (if (:preload s)
;;       (reset! p5-preload (:preload s)))
;;     (if (:setup s)
;;       (println "setup!!!")
;;       (reset! p5-setup (:setup s)))
;;     (reset! p5-draw (:draw s))
;;     (set! *warn-on-infer* false)
;;     (.preload @running-sketch)
;;     (.setup @running-sketch)))

;;Element to wrap p5 with
(def p5-wrapper (.getElementById js/document "p5-wrapper"))

;; This is needed because of something to do with google closure (theres a way to wrap this function that will get rid of the need for me to turn it off)
(set! *warn-on-infer* false)
(defn init-p5 []
  (reset! running-sketch (new p5
               (fn [p] ; fn creates an anonymous function, p is the sketch object
                 (set! (.-preload p) (fn [] (@p5-preload p)))
                 (set! (.-setup p) (fn [] (p5-init-setup p))) ; attach the setup method to the sketch
                 (set! (.-draw p) (fn [] (@p5-draw p))))  ; attach the draw method to the sketch
               p5-wrapper)))

(defn if-contains-execute [col key p]
  (if (contains? col key)
    ((key col) p)))

(defn init-sketch [s]
  (if @running-sketch (.remove @running-sketch))
  (reset! running-sketch
          (new p5
               (fn [p] ; fn creates an anonymous function, p is the sketch object
                 (set! (.-preload p) (fn [] (if-contains-execute s :preload p)))
                 (set! (.-setup p) (fn [] (do ((:setup s) p)
                                              (p5-hydra-setup p)))) ; attach the setup method to the sketch
                 (set! (.-draw p) (fn [] ((:draw s) p)))
                 (.remove p))
               p5-wrapper)))
