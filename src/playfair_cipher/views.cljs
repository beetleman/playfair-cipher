(ns playfair-cipher.views
  (:require [reagent.core :as reagent]
            [playfair-cipher.crypt :as crypt]))

;; views
(defn onchange-fn [state target]
  (fn [event]
    ()
    (swap! state assoc target (-> event .-target .-value))))


(defn input [state label target]
  [:fieldset
   [:label label ": "
    [:input {:placeholder "Write text.."
             :value (target @state)
             :on-change (onchange-fn state target)}]]])

(defn textarea [state label target]
  [:fieldset
   [:label label ": "
    [:textarea {:placeholder "Write text.."
                :value (target @state)
                :on-change (onchange-fn state target)}]]])


(defn crypt-view [state target title crypt-fn]
  [:div
   [:form.pure-form.crypt {:on-submit (fn [e] (.preventDefault e))}
    [:h2 title]
    [input state "Text" target]
    [:lable "Result:"
     [:pre.result
      (crypt-fn (crypt/create-table-with-key
                 (mapv char (:table-str @state))
                 (:key @state))
                (target @state)
                \f)]]]])


(defn main-view [state reset-state-fn table]
  [:div.app
   [:div.header
    [:h1 "Playfair Cipher"]]
   [:form.pure-form.settings {:on-submit (fn [e] (.preventDefault e))}
    [:h2 "Settings"]
    [input state "Key" :key]
    [textarea state "Chars used in table" :table-str]
    [:button.pure-button.pure-button-primary {:on-click #(reset-state-fn)}
     "Reset settings"]]
   [:div.pure-g
    [:div.pure-u-1-2
     [crypt-view state :encrypt "Encrypt" crypt/encrypt]]
    [:div.pure-u-1-2
     [crypt-view state :decrypt "Decrypt" crypt/decrypt]]]])
