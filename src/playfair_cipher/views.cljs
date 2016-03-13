(ns playfair-cipher.views
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [playfair-cipher.crypt :as crypt]))

;; views
(defn onchange-fn [state target]
  (fn [event]
    ()
    (swap! state assoc target (-> event .-target .-value))))



(defn input* [state label target tag]
  [:fieldset
   [:label label ": "
    [tag {:placeholder "Write text.."
          :value (target @state)
          :on-change (onchange-fn state target)}]]])

(defn input [state label target]
  (input* state label target :input))

(defn textarea [state label target]
  (input* state label target :textarea))

(defn table-view [table]
  [:div.table-wrapper
   [:table.pure-table.pure-table-bordered
    (into [:tbody]
          (for [row @table]
            (into [:tr] (for [cell row]
                          [:td cell]))))]])

(defn crypt-view [state table target title crypt-fn]
  [:div
   [:form.pure-form.crypt {:on-submit (fn [e] (.preventDefault e))}
    [:h2 title]
    [textarea state "Text" target]
    [:lable "Result:"
     [:textarea.result
      {:value (crypt-fn @table
                        (target @state)
                        (-> @state :key first))
       :readonly true}]]]])


(defn main-view [state reset-state-fn]
  (let [table (reaction (crypt/create-table-with-key
                         (mapv char (:table-str @state))
                         (:key @state)))]
    [:div.app
     [:div.header
      [:h1 "Playfair Cipher"]]
     [:form.pure-form.settings {:on-submit (fn [e] (.preventDefault e))}
      [:h2 "Settings"]
      [input state "Key" :key]
      [textarea state "Chars used in table" :table-str]
      [:button.pure-button.pure-button-primary {:on-click #(reset-state-fn)}
       "Reset settings"]
      [table-view table]]
     [:div.pure-g
      [:div.pure-u-1-2
       [crypt-view state table :encrypt "Encrypt" crypt/encrypt]]
      [:div.pure-u-1-2
       [crypt-view state table :decrypt "Decrypt" crypt/decrypt]]]]))
