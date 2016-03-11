(ns main-test
  (:require-macros [cljs.test :refer [deftest testing is async]])
  (:require [cljs.test]
            [playfair-cipher.core :as core]))

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
       (core/create-char-vector [\x \a \g \1 \z] [\g \z])
       [\g \z \x \a \1])))


(deftest create-table []
  (is (=
       (core/create-table test-vector)
       test-table)))

(deftest index-of []
  (is (=
       (core/index-of test-vector \q)
       4)))

(deftest split-word []
  (is (=
       (core/split-word "1q2w3" "X")
       [[\1 \q] [\2 \w] [\3 \X]])))

(deftest get-char-position []
  (is (=
       (core/get-char-position test-table \q)
       [1 0])))

(deftest get-chars-positions []
  (is (=
       (core/get-chars-positions test-table [[\1 \s] [\2 \x]])
       [[[0 0]
         [2 1]]
        [[0 1]
         [3 1]]])))

(deftest encrypt-position []
  (is (=
       (core/encrypt-position 4
                              [[0 1]
                               [3 1]])
       [[1 1]
        [0 1]])))


(deftest encrypt-positions []
  (is (=
       (core/encrypt-positions test-table
                               [[[0 0]
                                 [2 1]]
                                [[0 1]
                                 [3 1]]])
       [[[0 1]
         [2 0]]
        [[1 1]
         [0 1]]])))
