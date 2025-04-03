rs.initiate(
    {
        _id: "configReplSetRepoUser",
        configsvr: true,
        members: [
            { _id: 0, host: "mongo-repo-user-shard-0.mongo.svc.cluster.local:27017" },
        ]
    }
);
