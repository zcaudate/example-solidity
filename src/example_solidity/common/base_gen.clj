(ns example-solidity.common.base-compile
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.json :as json]
            [std.fs :as fs]
            [example-solidity.common.eth-test-erc20 :as erc20]
            [example-solidity.contract.escrow :as escrow]
            [example-solidity.contract.registry :as reg]
            [std.make :as make :refer [def.make]]))

(def.make PROJECT
  {:build    ".build/rt-solidity-contracts"
   :default
   [{:type   :contract.sol
     :lang   :solidity
     :target "solidity"
     :main   [escrow/+default-contract+]}
    {:type   :contract.sol
     :lang   :solidity
     :target "solidity"
     :main   [escrow/+default-contract+]}
    
    {:type   :contract.abi
     :lang   :solidity
     :target "interface"
     :main   [erc20/+default-contract+]}
    {:type   :contract.sol
     :lang   :solidity
     :target "solidity"
     :main   [erc20/+default-contract+]}
    
    {:type   :contract.abi
     :lang   :solidity
     :target "interface"
     :main   [reg/+default-contract+]}
    {:type   :contract.sol
     :lang   :solidity
     :target "solidity"
     :main   [reg/+default-contract+]}]})

(def +target+
  "core-assets/contract/core")

(defn compile-contracts
  []
  (std.fs/delete (str "resources/" +target+))
  (make/build PROJECT)
  (std.fs/move ".build/rt-solidity-contracts" (str "resources/" +target+)))

(defn compiled-abi-fn
  [path & [target]]
  (fn []
    (get (std.json/read
          (h/sys:resource-content
           (str (or target +target+) path)))
         "abi")))

(defn compiled-bytecode-fn
  [path & [target access]]
  (fn []
    (get-in (std.json/read
             (h/sys:resource-content
              (str (or target +target+) path)))
            (or access
                ["evm" "bytecode" "object"]))))

;;
;; SINGLE
;;

(def ^{:arglists '([])}
  get-escrow-abi
  (compiled-abi-fn "/interface/rt-solidityEscrow.json"))

(def ^{:arglists '([])}
  get-escrow-bytecode
  (compiled-bytecode-fn "/interface/rt-solidityEscrow.json"))

(comment
  (make/build-all PROJECT)
  (compile-contracts)
  
  (get-escrow-abi)
  (get-vault-erc20-abi))


