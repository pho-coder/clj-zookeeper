(ns clj-zookeeper.zookeeper-test
  (:require [clojure.test :refer :all]
            [clj-zookeeper.zookeeper :refer :all])
  (:import [java.util UUID]
           [org.apache.curator.test TestingServer]))

(defn setup-embedded-zk [f]
  (let [server (TestingServer. 2181)]
    (do (f)
        (.close server))))

(use-fixtures :once setup-embedded-zk)

(def connect-string "127.0.0.1:2181")

(deftest dsl-test
  (let [parent-node (str "/test-" (UUID/randomUUID))
        child-node-prefix "child-"
        child0 (str child-node-prefix "0000000000")
        child1 (str child-node-prefix "0000000001")
        client (new-client connect-string)
        data-string "test data"]
    ;;creation tests
    (is (= false (check-exists? parent-node)))
    (is (= false (check-exists? parent-node)))
    (do (create parent-node :data (.getBytes "ad"))
        (is (= true (check-exists? parent-node))))
    (do (delete parent-node)
        (is (= false (check-exists? parent-node))))
    (close)))
