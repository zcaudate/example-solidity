(ns example-solidity.common.hello
  (:require [std.html :as html]))


(defn add-10
  "adds two numbers plus 10
 
   (add-10 1 2)
   => 12"
  {:added "0.1"}
  [x y]
  (+ x y 10))

(defn add-100
  "adds two numbers plus 100
 
   (add-100 1 2)
   => 103"
  {:added "0.1"}
  [x y]
  (+ x y 100))

(comment
  
  (defn add-10
    [x y]
    (+ x y 10))

  (add-10 1 2))
