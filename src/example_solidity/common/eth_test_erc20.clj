^{:no-test true}
(ns example-solidity.common.eth-test-erc20
  (:require [std.lang :as l]
            [std.lib :as h])
  (:refer-clojure :exclude [name symbol]))

(l/script :solidity
  {:require [[rt.solidity :as s]]
   :static {:contract ["ERC20Basic"]}})

(def.sol ^{:- [:string :public]}
  name)

(def.sol ^{:- [:string :public]}
  symbol)

(def.sol ^{:- [:uint8 :public]}
  decimals)

(defevent.sol Event
  [:string  event-type
   :address from-address
   :address to-address
   :uint amount])

(defmapping.sol ^{:- [:public]}
  balanceOf
  [:address :uint])

(defmapping.sol ^{:- [:public]}
  allowance
  [:address (:mapping [:address :uint])])

(def.sol ^{:- [:uint :public]}
  totalSupply)

(defconstructor.sol
  __init__
  [:uint _total
   :string :memory _symbol
   :string :memory _name
   :uint8 _decimals]
  (:= -/totalSupply _total)
  (:= -/symbol _symbol)
  (:= -/name _name)
  (:= -/decimals _decimals)
  (:= (. balanceOf [s/msg-sender])
      _total))

(defn.sol ^{:- [:external]
            :static/returns :bool}
  transfer
  [:address receiver
   :uint amount]
  (:-= (. -/balanceOf [s/msg-sender]) amount)
  (emit (-/Event {:event-type "transfer"
                  :from-address s/msg-sender
                  :to-address receiver
                  :amount amount}))
  (:+= (. -/balanceOf [receiver]) amount)
  (return true))

(defn.sol ^{:- [:external]}
  transferFrom
  [:address from
   :address receiver
   :uint amount]
  (:-= (. -/allowance  [from] [s/msg-sender]) amount)
  (emit (-/Event {:event-type "transfer_credit"
                  :from-address from
                  :to-address receiver
                  :amount amount}))
  (:+= (. -/balanceOf [receiver]) amount))

(defn.sol ^{:- [:external]}
  approve
  [:address spender
   :uint amount]
  (:-= (. -/balanceOf [s/msg-sender]) amount)
  (emit (-/Event {:event-type "approve"
                  :from-address s/msg-sender
                  :to-address spender
                  :amount amount}))
  (:+= (. -/allowance  [s/msg-sender] [spender]) amount))

;;
;; Contract
;;

(def +default-contract+
  {:ns   (h/ns-sym)
   :name "ERC20Basic"
   :args ["100000000000000000000000000000"
          "ERC20Basic"
          "Basic"
          18]})
