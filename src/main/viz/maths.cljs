(ns viz.maths
 (:require-macros [infix.macros :refer [infix]]))

(defn scale [n omin omax]
  (infix n * (omax - omin) / 127 + omin))
