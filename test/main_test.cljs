(ns main-test
  (:require-macros [cljs.test :refer [deftest testing is async]])
  (:require [cljs.test]
            [playfair-cipher.core :as core]))

(deftest test-pass []
  (is (= 2 2)))
