(ns viz.midi
  (:require ["webmidi" :as web-midi]
            [viz.maths :refer [scale]]))

;; Lists all noteon messages
;; e.note.number will give you the note number for switching based on noteon message.
;; (.addListener quneo-input "noteon" "all"
;;               (fn [e] (js/console.log e)))

;; Lists all controlchange messages (really rinses the console as quneo puts out one for pressure as well as location which is probably the one I actually want.)
;; e.controller.number and e.value are the properties I want
;; filter based on number, update based on value

;; Some of the pads have the same cc for pressure as the sliders do for location so may need to filter based on another criteria as well or set up a preset in the software.
;; Need to create a custom preset in the quneo software that disables pressure for grid mode pads

;; (.addListener quneo-input "controlchange" "all"
;;               (fn [e] (js/console.log e)))

(def sliders (atom {:slider1 0
                    :slider2 0
                    :slider3 0
                    :slider4 0}))
(def buttons (atom {0 #(js/console.log 1)
                    1 #(js/console.log 2)
                    2 false
                    3 false
                    4 false
                    5 false
                    6 false
                    7 false
                    }))
(defn set-button [key f]
  (swap! buttons assoc key f ))

(defn clear-buttons []
  (reset! buttons {}))
(clear-buttons)
(defn get-val [atom]
  @atom)

;; web midi setup
(defn get-num [e]
  (.. e -controller -number))
(defn get-note-num [e]
  (.. e -note -number))
(defn update-slider [key e]
  (swap! sliders assoc key (.-value e)))


;; (defn start-midi []
;;   (.enable
;;    web-midi
;;    (fn []
;;      ;; Different OS's use different names for these midi devices

;;      (let [midi-input (.getInputByName web-midi "AudioSwift 3")]
;;        ;; cc listeners
;;        (js/console.log (.-inputs web-midi))
;;        (.addListener midi-input "controlchange" "all"
;;                      (fn [e]
;;                        (let [s-num (get-num e)]

;;                          (js/console.log s-num)
;;                          (cond (= 1 s-num)
;;                                (update-slider :slider1 e)
;;                                (= 2 s-num)
;;                                (update-slider :slider2 e)
;;                                (= 3 s-num)
;;                                (update-slider :slider3 e)
;;                                (= 4 s-num)
;;                                (update-slider :slider4 e)))))
;;        ))))
(defn start-midi []
  (.enable
   web-midi
   (fn []
     ;; Different OS's use different names for these midi devices

     (js/console.log (.-inputs web-midi))
     (let [quneo-input (.getInputByName web-midi "QUNEO")]
       ;; cc listeners
       (.addListener quneo-input "controlchange" "all"
                     (fn [e]
                       ;; (js/console.log "CC!!")
                       (cond (= 6 (get-num e))
                                   (update-slider :slider1 e)
                                   (= 7 (get-num e))
                                   (update-slider :slider2 e)
                                   (= 8 (get-num e))
                                   (update-slider :slider3 e)
                                   (= 9 (get-num e))
                                   (update-slider :slider4 e))))
       (.addListener quneo-input "noteon" "all"
                     (fn [e]
                       ;; (js/console.log "button press")
                       (let [num (get-note-num e)
                             b (get @buttons num)]
                         (if b (b))
                         )))
       ))))

(defn s [key min max]
  (scale (key @sliders) min max))
