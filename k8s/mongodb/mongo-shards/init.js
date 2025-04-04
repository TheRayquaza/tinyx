rs.initiate(
    {
        _id: "shardReplSet",
        configsvr: true,
        members: [
            { _id: 0, host: "mongo-shard-0.mongo-shard.mongo.svc.cluster.local:27018" },
            { _id: 1, host: "mongo-shard-1.mongo-shard.mongo.svc.cluster.local:27018" },
            //{ _id: 2, host: "mongo-shard-2.mongo-shard.mongo.svc.cluster.local:27018" }
        ]
    }
);
