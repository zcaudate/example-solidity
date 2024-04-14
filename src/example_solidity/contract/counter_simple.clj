(ns example-solidity.contract.counter-simple
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :solidity
  {:require [[rt.solidity :as s]]})

(defenum.sol CounterLogType
  [:Inc :Dec :Add :Sub :Mul])

(defevent.sol CounterLog
  [:address user]
  [:% -/CounterLogType eventType])

(def.sol ^{:- [:uint :public]}
  g:Counter)

(defn.sol ^{:- [:public :view]
            :static/returns [:uint]}
  get-counter
  "gets the 0 counter"
  {:added "4.0"}
  []
  (return -/g:Counter))

(defn.sol ^{:- [:public]}
  inc-counter
  "increments counter 0"
  {:added "4.0"}
  []
  (:= -/g:Counter (+ -/g:Counter 1))
  (emit (-/CounterLog s/msg-sender
                      (. -/CounterLogType Inc))))

(defn.sol ^{:- [:public]}
  dec-counter
  "decrements counter 0"
  {:added "4.0"}
  []
  (:= -/g:Counter (- -/g:Counter 1))
  (emit (-/CounterLog s/msg-sender
                 (. -/CounterLogType Dec))))

(defn.sol ^{:- [:public]}
  add-counter
  "increments counter 0"
  {:added "4.0"}
  [:uint x]
  (:= -/g:Counter (+ -/g:Counter x))
  (emit (-/CounterLog s/msg-sender
                      (. -/CounterLogType Add))))

(defn.sol ^{:- [:public]}
  sub-counter
  "increments counter 0"
  {:added "4.0"}
  [:uint x]
  (:= -/g:Counter (- -/g:Counter x))
  (emit (-/CounterLog s/msg-sender
                      (. -/CounterLogType Sub))))

(defn.sol ^{:- [:public]}
  mul-counter
  "increments counter 0"
  {:added "4.0"}
  [:uint x]
  (:= -/g:Counter (* -/g:Counter x))
  (emit (-/CounterLog s/msg-sender
                      (. -/CounterLogType Mul))))

(def +default-contract+
  {:ns   (h/ns-sym)
   :name "CounterSimple"
   :args []})
