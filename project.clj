(defproject clj-zookeeper "0.4.0-SNAPSHOT"
  :description "A Clojure DSL for Apache ZooKeeper Curator Framework"
  :url "https://github.com/pho-coder/clj-zookeeper"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.zookeeper/zookeeper "3.4.10"]
                 [org.apache.curator/curator-framework "2.12.0"]
                 [org.apache.curator/curator-client "2.12.0" :scope "retry"]]
  :profiles {:dev {:dependencies [[org.apache.curator/curator-test "2.12.0" :scope "test"]]}})
