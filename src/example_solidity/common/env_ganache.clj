^{:no-test true}
(ns example-solidity.common.env-ganache
  (:require [std.lib :as h :refer [defimpl]]
            [std.json :as json]
            [std.string :as str]
            [std.lang :as l]
            [std.fs :as fs]
            [rt.basic.impl.process-js :as process-js]
            [rt.solidity.compile-common :as common]))

(defonce +contracts+ (atom {}))

(defn- clear-contracts
  []
  (reset! +contracts+ {}))

(def +default-port+ 8545)

(def +default-dir+ "test-bench/ganache/rt-solidity")

(def +default-mnemonic+
  "child husband quiz option cousin excuse coyote start width atom rubber spoon")

(def +default-addresses-raw+
  ["0xfcA2B4E8b7bFE53402A8C653ba002817670F100b"
   "0x871A4F413fA0079EEE4c2f8c2E98d7166Db26f7D"
   "0x92bBBCf0E667B0D37EEB047e7eE770a6FFEaB5FC"
   "0xf46Cd230C6103bCb1752B304f326cd33e28fDAa0"
   "0x205423A4f53B5Effe9A24094FBd2A995325BB98B"
   "0xb10f4AD153Be78E603a20Ff0d9f4fC39aBd17e42"
   "0xBD2113E2CB4A1698dd5f97aeaF61f5fc76B8df70"
   "0xF45d2258215b24320104f99E2C1a9fb0F5BE6fFF"
   "0x746f6cb52F1fb3Dc3D91700337e03e08EB8Af86B"
   "0xb9f2a32d4B9F2aB39bB4321F331e3BD3b77F899C"])

(def +default-addresses+
  (mapv (fn [s]
          (str "0x" (str/upper-case (subs s 2))))
        +default-addresses-raw+))

(def +default-private-keys+
  ["0x3948b4091a200fbaad337ba79415442f5785054c76bfe176ee4d0446fe8d3461"
   "0x2f2633d6cde9bfc4529c43953ec95e99ca1c5d4f88473addb7d4838f39008ec8"
   "0x43d00974779a15fca11054540e452b9fe583c74838e7f15968063db5e6f4676d"
   "0x26a583d185ecd7bdb1ca70374d1ff7f18e757a07362a5b541a71f9fe814d1fec"
   "0xd72333020e663e7b549ee4974e72f2d5294501ad4aaa0fbd400e2778f1c05e04"
   "0xb7b3f480e3faf133b152710b90d502ef19082c169d2f852c979f089c2ab8f51b"
   "0xbf8984f52d70bf4766fb297ed6fb346869847a856e62b2575e5977bacd65caf1"
   "0x02955f1a719f5c3d5a8858a09d20732352263e1ac8464b57cf55ae49ff5c8080"
   "0x17303c20fa12e3819e2800fbff933c3d29aa6772def39071133d32ffa4511e20"
   "0xbe6317a13c50ffc6b658feb59dad5d736f4a37d56ca12072484c6271f365dce8"])

(defonce ^:dynamic *server* (atom nil))

(def ^:dynamic *program-cmd*
  (str/join " "
            ["ganache"
             "--accounts=10"
             "--wallet.seed 'rt-solidity/rt-solidity.testnet'"
             "--gasLimit 1000000000000000000"
             "--host '0.0.0.0'"]))

(defn start-ganache-server
  "starts the ganache server in a given directory"
  {:added "4.0"}
  [& [args]]
  (or @*server*
      (when (h/port:check-available +default-port+)
        (let [_    (fs/create-directory +default-dir+)
              _    (clear-contracts)]
          (-> (if (not @*server*)
                (swap! *server*
                       (fn [m]
                         (let [process (h/sh {:args (or args
                                                        ["/bin/bash" "-c" *program-cmd*])
                                              :wait false
                                              :root +default-dir+})
                               thread  (-> (h/future
                                             (h/sh-wait process))
                                           (h/on:complete (fn [_ _]
                                                            (reset! *server* nil))))]
                           (h/wait-for-port "127.0.0.1" +default-port+
                                            {:timeout 5000})
                           {:type "ganache"
                            :port +default-port+
                            :root +default-port+
                            :process process
                            :thread thread})))
                @*server*))))))

(defn stop-ganache-server
  "stop the ganache server"
  {:added "4.0"}
  []
  (let [{:keys [type process] :as entry} @*server*]
    (when process
      (doto process
        (h/sh-close)
        (h/sh-exit)
        (h/sh-wait)))
    entry))

(def +init+
  (do (alter-var-root  #'common/*default-caller-private-key*
                       (fn [_]
                         (first +default-private-keys+)))
      (alter-var-root  #'common/*default-caller-address*
                       (fn [_]
                         (first +default-addresses+)))))
