(ns playfair-cipher.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent :refer [atom]]
            [playfair-cipher.crypt :as crypt]
            [playfair-cipher.logger :as logger]
            [playfair-cipher.views :as views]))



;; ------------------------
;; app state

(def default-key "1kjkjn")
(def default-table-str "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
(def default-app-state {:encrypt ""
                        :decrypt ""
                        :key default-key
                        :table-str default-table-str})
(def app-state (atom default-app-state))
(add-watch app-state :logger #(logger/debug :app-state %4))

(defn reset-app-state [& args]
  (reset! app-state default-app-state))


;; ------------------------
;; mounting app

(defn mount-root []
  (reagent/render [views/main-view app-state reset-app-state]
                  (.getElementById js/document "playfair-cipher")))

(defn main []
  (mount-root))
