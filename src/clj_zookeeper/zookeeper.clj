(ns clj-zookeeper.zookeeper
  (:import (org.apache.zookeeper CreateMode ZooDefs$Ids)
           (org.apache.curator.framework CuratorFramework CuratorFrameworkFactory)
           (org.apache.curator.retry BoundedExponentialBackoffRetry)))

(def ^:dynamic *curator-framework* (atom nil))

(def zk-create-modes
  {:ephemeral CreateMode/EPHEMERAL
   :persistent CreateMode/PERSISTENT
   :persistent-sequential CreateMode/PERSISTENT_SEQUENTIAL
   :ephemeral-sequential CreateMode/EPHEMERAL_SEQUENTIAL})

(defn new-client
  [zk-str & {:keys [base-sleep-timems max-sleep-timems max-retries
                    connection-timeoutms session-timeoutms]
             :or {base-sleep-timems 1000
                  max-sleep-timems 30000
                  max-retries 5
                  connection-timeoutms 15000
                  session-timeoutms 20000}}]
  (let [builder (.. (CuratorFrameworkFactory/builder)
                    (connectString zk-str)
                    (retryPolicy (BoundedExponentialBackoffRetry. base-sleep-timems
                                                                  max-sleep-timems
                                                                  max-retries))
                    (connectionTimeoutMs connection-timeoutms)
                    (sessionTimeoutMs session-timeoutms))
        curator-framework (.build builder)]
    (.start curator-framework)
    (reset! *curator-framework* curator-framework)
    curator-framework))

(defn close
  []
  (.close @*curator-framework*))

(defn create
  [^String path & {:keys [mode data]
                   :or {mode :persistent}}]
  (if (nil? data)
    (.. @*curator-framework*
        (create)
        (withMode (zk-create-modes mode))
        (withACL ZooDefs$Ids/OPEN_ACL_UNSAFE)
        (forPath path))
    (.. @*curator-framework*
        (create)
        (withMode (zk-create-modes mode))
        (withACL ZooDefs$Ids/OPEN_ACL_UNSAFE)
        (forPath path data))))

(defn delete
  [^String path]
  (.. @*curator-framework*
      (delete)
      (forPath path)))

(defn check-exists?
  [^String path]
  ((complement nil?)
   (.. @*curator-framework* (checkExists) (forPath path))))

(defn get-children
  [^String path]
  (.. @*curator-framework* (getChildren) (forPath path)))

(defn get-data
  [^String path]
  (if (check-exists? path)
    (.. @*curator-framework*
        (getData)
        (forPath path))))

(defn set-data
  [^String path ^bytes data]
  (.. @*curator-framework* (setData) (forPath path data)))
