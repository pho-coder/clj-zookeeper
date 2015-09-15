(defproject clj-zookeeper "0.1.0-SNAPSHOT"
  :description "A Clojure DSL for Apache ZooKeeper Curator Framework"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.apache.zookeeper/zookeeper "3.4.6"]
                 [org.apache.curator/curator-framework "2.9.0"]
                 [org.apache.curator/curator-client "2.9.0"]
                 [org.apache.curator/curator-test "2.9.0" :scope "test"]])
