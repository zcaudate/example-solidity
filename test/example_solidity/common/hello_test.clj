(ns example-solidity.common.hello-test
  (:use code.test)
  (:require [example-solidity.common.hello :refer :all]))

^{:refer example-solidity.common.hello/add-10 :added "0.1"}
(fact "adds two numbers plus 10"

  (add-10 1 2)
  => 12)

^{:refer example-solidity.common.hello/add-100 :added "0.1"}
(fact "adds two numbers plus 100"

  (add-100 1 2)
  => 103)
