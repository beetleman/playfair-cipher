(ns main-test
  (:require-macros [cljs.test :refer [deftest testing is async]])
  (:require [cljs.test]
            [playfair-cipher.core :as core]
            [playfair-cipher.crypt :as crypt]))

(def test-vector
  [\1 \2 \3 \4
   \q \w \e \r
   \a \s \d \f
   \z \x \c \v
   \0])

(def test-table
  [[\1 \2 \3 \4]
   [\q \w \e \r]
   [\a \s \d \f]
   [\z \x \c \v]])


(deftest test-pass []
  (is (= 2 2)))


(deftest create-char-vector []
  (is (=
       (crypt/create-char-vector [\x \a \g \1 \z] [\g \z])
       [\g \z \x \a \1])))


(deftest create-table []
  (is (=
       (crypt/create-table test-vector)
       test-table)))

(deftest index-of []
  (is (=
       (crypt/index-of test-vector \q)
       4)))

(deftest split-word []
  (is (=
       (crypt/split-word "X" "1q2w3")
       [[\1 \q] [\2 \w] [\3 \X]]))
  (is (=
       (crypt/split-word "X" "1q2w")
       [[\1 \q] [\2 \w]])))

(deftest char->position []
  (is (=
       (crypt/char->position test-table \q)
       [1 0])))

(deftest chars->positions []
  (is (=
       (crypt/chars->positions test-table [[\1 \s] [\2 \x]])
       [[[0 0]
         [2 1]]
        [[0 1]
         [3 1]]])))

(deftest positions->chars []
  (is (=
       (crypt/positions->chars test-table [[[0 0]
                                           [2 1]]
                                          [[0 1]
                                           [3 1]]])
       [[\1 \s] [\2 \x]])))

(deftest encrypt-position []
  (is (=
       (crypt/encrypt-position 4
                              [[0 1]
                               [3 1]])
       [[1 1]
        [0 1]])))


(deftest encrypt-positions []
  (is (=
       (crypt/encrypt-positions test-table
                               [[[0 0]
                                 [2 1]]
                                [[0 1]
                                 [3 1]]])
       [[[0 1]
         [2 0]]
        [[1 1]
         [0 1]]])))


(deftest crypt-symetry []
  (let [word "qwer"
        fill-letter \f
        word-encrypted (crypt/encrypt test-table word fill-letter)
        word-decrypted (crypt/decrypt test-table word-encrypted fill-letter)
        ]
   (is (=
        word-decrypted
        word))))
