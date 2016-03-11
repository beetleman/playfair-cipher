(ns playfair-cipher.core
  (:require [reagent.core :as reagent :refer [atom]]))


(def default-key "!JKBKU KBKK LLkhvbV")

;; app state

(def app-state (atom {:text "" :key ""}))
(add-watch app-state :logger #(-> %4 clj->js js/console.debug))

;; ------------------------
;; table
(def utf-8-num (range 65535))
(def utf-8-char (mapv char utf-8-num))

(defn create-char-vector [table key]
  (let [key (vec key)
        key-set (set key)
        table (filterv #((complement contains?) key-set %) table)]
    (into key table)))


(defn create-table [v]
  (partition (-> (count v) Math/sqrt int) v))

;; ------------------------
;; Playfair Cipher
(defn index-of [coll x]
  (js->clj (.indexOf (clj->js coll) x)))

(defn split-word [l word]
  "split `word' into pairs, if odd numer of letter ausing `l' character"
  (let [word (vec word)
        word (if (odd? (count word))
               (conj word l)
               word)]
    (partition 2 word)))

(defn join-word [l chars]
  (let [word (flatten chars)]
    (if (= l (last word))
      (apply str (drop-last word))
      (apply str word))))


(defn char->position [table char]
  (first (filter
          #(not= -1 (second %))
          (map-indexed (fn [idx itm] [idx (index-of itm char)]) table))))

(defn chars->positions [table splited-word]
  (map
   #(map (partial char->position table) %)
   splited-word))

(defn positions->chars [table positions]
  (map (fn [itm] (map #(get-in table %) itm))
       positions))

(defn crypt-position* [max-idx position next-index-fn]
  (let [[[l1-x l1-y] [l2-x l2-y]] position
        allowed-idexies (cycle (range max-idx))
        next-index (fn [idx] (nth allowed-idexies
                                  (+ max-idx (next-index-fn idx))))]
    (cond
      (= l1-x l2-x)
      [[l1-x (next-index l1-y)]
       [l2-x (next-index l2-y)]]
      (= l1-y l2-y)
      [[(next-index l1-x) l1-y]
       [(next-index l2-x) l2-y]]
      :else
      [[l1-x l2-y]
       [l2-x l1-y]])))

(defn encrypt-position [max-idx position]
  (crypt-position* max-idx position inc))


(defn decrypt-position [max-idx position]
  (crypt-position* max-idx position dec))


(defn crypt-positions* [table positions conv-fn]
  (map (partial conv-fn (-> table first count))
       positions))

(defn encrypt-positions [table positions]
  (crypt-positions* table positions encrypt-position))


(defn decrypt-positions [table positions]
  (crypt-positions* table positions decrypt-position))


(defn crypt* [table word fill-letter crypt-fn]
  (->> word
       (split-word fill-letter)
       (chars->positions table)
       (crypt-fn table)
       (positions->chars table)
       (join-word fill-letter))
  )

(defn encrypt [table word fill-letter]
  (crypt* table word fill-letter encrypt-positions))


(defn decrypt [table word fill-letter]
  (crypt* table word fill-letter decrypt-positions))

;; views


(defn input-onchange-fn [state target]
  (fn [event]
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
