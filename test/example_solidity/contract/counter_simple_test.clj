(ns example-solidity.contract.counter-simple-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [example-solidity.common.env-ganache :as env]))

(l/script- :solidity
  {:runtime :web3
   :require [[rt.solidity :as s]
             [example-solidity.contract.counter-simple :as counter]]})

(fact:global
 {:setup [(do (env/start-ganache-server)
              (Thread/sleep 1000)
              (env/stop-ganache-server)
              (Thread/sleep 500)
              (l/rt:restart))]
  :teardown [(l/rt:stop)]})

^{:refer example-solidity.contract.counter-simple/CANARY :adopt true :added "4.0"}
(fact "determanistic gas fees"
  ^:hidden

  (s/with:measure
    (s/rt:deploy counter/+default-contract+))
  => (contains-in [(approx 1 0.5) map?]))

^{:refer example-solidity.contract.counter-simple/get-counter :added "0.1"
  :setup [(s/rt:deploy counter/+default-contract+)]}
(fact "gets the counter"
  ^:hidden

  (do (counter/inc-counter)
      (counter/get-counter))
  => 1)

^{:refer example-solidity.contract.counter-simple/inc-counter :added "0.1"
  :setup [(s/rt:deploy counter/+default-contract+)]}
(fact "increments the counter"
  ^:hidden

  (do (dotimes [i 10]
        (counter/inc-counter))
      (counter/get-counter))
  => 10)

^{:refer example-solidity.contract.counter-simple/dec-counter :added "0.1"
  :setup [(s/rt:deploy counter/+default-contract+)]}
(fact "decrements the counter"
  ^:hidden

  (do (counter/add-counter 10)
      (counter/dec-counter)
      (counter/get-counter))
  => 9)

^{:refer example-solidity.contract.counter-simple/add-counter :added "0.1"
  :setup [(s/rt:deploy counter/+default-contract+)]}
(fact "adds a number to the counter"
  ^:hidden

  (do (counter/add-counter 10)
      (counter/get-counter))
  => 10)

^{:refer example-solidity.contract.counter-simple/sub-counter :added "0.1"
  :setup [(s/rt:deploy counter/+default-contract+)]}
(fact "substracts a number from the counter"
  ^:hidden

  (do (counter/add-counter 10)
      (counter/sub-counter 5)
      (counter/get-counter))
  => 5)

^{:refer example-solidity.contract.counter-simple/mul-counter :added "0.1"
  :setup [(s/rt:deploy counter/+default-contract+)]}
(fact "multiplies the counter by a number"
  ^:hidden

  (do (counter/add-counter 10)
      (counter/mul-counter 10)
      (counter/get-counter))
  => 100)

(comment
  (s/rt:send-wei (nth env/+default-addresses+ 9) 100000)
  (s/rt:node-get-balance (nth env/+default-addresses+ 0))
  (s/rt:node-get-balance (nth env/+default-addresses+ 9)))

(comment

  (counter/add-counter 10)
  (counter/sub-counter 10)
  (counter/add-counter 10)
  (counter/get-counter)
  (counter/g:Counter))
