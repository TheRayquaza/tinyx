rs.initiate(
    {
        _id: "configReplSet",
        configsvr: true,
        members: [
            { _id: 0, host: "mongo-config-0.mongo-config.mongo.svc.cluster.local:27019" },
            //{ _id: 1, host: "mongo-config-1.mongo-config.mongo.svc.cluster.local:27019" },
            //{ _id: 0, host: "mongo-config-2.mongo-config.mongo.svc.cluster.local:27019" },
        ]
    }
);
