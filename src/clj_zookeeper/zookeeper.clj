(ns clj-zookeeper.zookeeper
  (:import (org.apache.zookeeper CreateMode ZooDefs$Ids)
           (org.apache.curator.framework CuratorFramework CuratorFrameworkFactory)
           (org.apache.curator.retry BoundedExponentialBackoffRetry)))

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
    curator-framework))

(defn close
  [^CuratorFramework client]
  (.close client))

(defn create
  [^CuratorFramework cf ^String path & {:keys [mode data]
                                        :or {mode :persistent}}]
  (if (nil? data)
    (.. cf
        (create)
        (withMode (zk-create-modes mode))
        (withACL ZooDefs$Ids/OPEN_ACL_UNSAFE)
        (forPath path))
    (.. cf
        (create)
        (withMode (zk-create-modes mode))
        (withACL ZooDefs$Ids/OPEN_ACL_UNSAFE)
        (forPath path data))))

(defn delete
  [^CuratorFramework cf ^String path]
  (.. cf
      (delete)
      (forPath path)))

(defn check-exists?
  [^CuratorFramework cf ^String path]
  ((complement nil?)
   (.. cf (checkExists) (forPath path))))

(defn get-children
  [^CuratorFramework cf ^String path]
  (.. cf (getChildren) (forPath path)))

(defn get-data
  [^CuratorFramework cf ^String path]
  (if (check-exists? cf path)
    (.. cf
        (getData)
        (forPath path))))

(defn set-data
  [^CuratorFramework cf ^String path ^bytes data]
  (.. cf (setData) (forPath path data)))
