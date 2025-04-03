rs.initiate(
    {
        _id: "cfgReplicaSetRepoUserShards",
        configsvr: true,
        members: [
            { _id: 0, host: "mongo-repo-user-shard-host-0.mongo.svc.cluster.local:27017" },
            { _id: 1, host: "mongo-repo-user-shard-host-1.mongo.svc.cluster.local:27017" },
            { _id: 2, host: "mongo-repo-user-shard-host-2.mongo.svc.cluster.local:27017" }
        ]
    }
);
