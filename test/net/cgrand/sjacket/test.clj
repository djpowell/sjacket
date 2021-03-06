(ns net.cgrand.sjacket.test
  (:use [clojure.test :only [deftest is are]])
  (:require [net.cgrand.sjacket :as sj]
            [net.cgrand.sjacket.parser :as p]))

(def input1
"(z (a ;comment
    b)) (4/2
         d))")

;; fractional offsets allow to uniquely identify a character without adding a
;; bias argument

(deftest rename1
  (is (= (sj/transform-src input1 1.5 (constantly 'zoo))
"(zoo (a ;comment
      b)) (4/2
           d))")))

(deftest wrap1
  (is (= (sj/transform-src input1 1.5 list)
"((z) (a ;comment
      b)) (4/2
           d))")))

(deftest reader-literals
  (let [instant "#inst \"2012-09-13T01:00:36.439-00:00\""
        parse-tree (p/parser instant)]
    (is (= 1 (count (:content parse-tree))))
    (is (= :reader-literal (:tag (first (:content parse-tree))))))

  (let [poorly-spaced-literal "#
                             foo { 1 2, 3
                              4}"
        parse-tree (p/parser poorly-spaced-literal)]
    (is (= 1 (count (:content parse-tree))))
    (is (= :reader-literal (:tag (first (:content parse-tree)))))))

(deftest destructuring-proof
  (is (= (sj/transform-src input1 3.5 (fn [[a b]] (list 'fn [] a b)))
"(z (fn [] a ;comment
    b)) (4/2
         d))")))

(def incomplete-string-input
"\"Hi,
")

(deftest incomplete-strings
  (is (= :net.cgrand.parsley/unfinished
         (:tag (p/parser incomplete-string-input))))
  (is (= incomplete-string-input
         (sj/str-pt (p/parser incomplete-string-input)))))

