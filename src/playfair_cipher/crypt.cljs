(ns playfair-cipher.crypt
  (:require [playfair-cipher.logger :as logger]))

;; ------------------------
;; table

(defn create-char-vector [v key]
  (let [key-v (vec key)
        key-set (set key)
        v (filterv #((complement contains?) key-set %) v)]
    (into key-v v)))

(defn length->size [l]
  (let [f (fn [x]
            (let [y (/ l x)]
              [(integer? y) x y]))
        max-y (-> l Math/sqrt int inc)]
    (->>
     (map f (range 1 max-y))
     (filter first)
     last
     rest)))


(defn create-table [v]
  (let [[_ max-y] (length->size (count v))]
   (partition max-y v)))

(defn create-table-with-key [v key]
  (create-table (create-char-vector v key)))


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
  (map (fn [itm] (map (fn [x]
                        (nth (nth table (first x)) (second x))) itm))
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
  (map (partial conv-fn (-> table count))
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
       (join-word fill-letter)))

(defn encrypt [table word fill-letter]
  (crypt* table word fill-letter encrypt-positions))


(defn decrypt [table word fill-letter]
  (crypt* table word fill-letter decrypt-positions))
