(ns viz.macro)

;; Be really careful as if the macro throws up a bug the shadow cljs repl doesnt show an error and will just use the last compiled version of the macro file

(defmacro h
  [& signal-path]
  `(.. js/window ~@signal-path))
