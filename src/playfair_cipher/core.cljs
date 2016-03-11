(ns playfair-cipher.core
  (:require [reagent.core :as reagent :refer [atom]]))


(def default-key "!JKBKU KBKK LLkhvbV")
(def utf-8-num (range 65535))
(def utf-8-char (mapv char utf-8-num))

;; app state

(def app-state (atom {:text "" :key ""}))
(add-watch app-state :logger #(-> %4 clj->js js/console.debug))

;; ------------------------
;; views


(defn input-onchange-fn [state target]
  (fn [event]
    (-> (char 999) clj->js js/console.debug)
    (swap! state assoc target (-> event .-target .-value))))


(defn input
  ([state label target] (input state label target ""))
  ([state label target value]
   [:fieldset
    [:label label ": "
     [:input {:placeholder "Write text.."
              :value value
              :on-change (input-onchange-fn state target)}]]]))


(defn main-view [state]
  [:div.app
   [:form.pure-form
    [:h1 "Playfair Cipher"]
    [input state "Text" :text]
    [input state "Key" :key default-key]]])


;; ------------------------
;; mounting app

(defn mount-root []
  (reagent/render [main-view app-state]
                  (.getElementById js/document "playfair-cipher")))

(defn main []
  (mount-root))
