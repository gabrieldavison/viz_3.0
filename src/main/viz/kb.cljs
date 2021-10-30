(ns viz.kb)

(def kb (atom {"0" #(js/console.log 0)
               "1" #(js/console.log 1)}))
(defn set-k [k f]
  (swap! kb assoc (str k) f))
(defn setup-kb []
  (.addEventListener
   js/document
   "keypress"
   (fn [e]
     (let [k (.-key e)
           kf (get @kb k)]
       (if kf (kf))))))
