(ns playfair-cipher.logger)

(defn debug [key x]
  (-> {key x} clj->js js/console.debug))
