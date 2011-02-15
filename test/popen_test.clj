(ns popen-test
  (:use [popen] :reload-all)
  (:use [clojure.test])
  (:require [clojure.java.io :as io]))

(deftest popen-test
  (let [p (popen/popen ["ls" "/tmp"])]
    (is (instance? Process p))
    (is (not (nil? (popen/stdout p))))
    (is (not (nil? (popen/stderr p))))
    (is (zero? (popen/join p)))
    (is (zero? (popen/exit-code p)))
    (is (nil? (popen/kill p)))))

(deftest read-test
  (let [p (popen/popen ["cat"])
        out (popen/stdout p)
        in (popen/stdin p)]
    (binding [*out* in]
      (println 1)
      (println 2))
    (.close in)
    (is (zero? (popen/exit-code p)))
    (is (= (line-seq (io/reader out)) '("1" "2")))))

(deftest run-and-kill-test
  (let [p (popen/popen ["cat"])]
    (is (popen/running? p))
    (popen/kill p)
    (is (not (running? p)))
    (is (not (zero? (exit-code p))))))

