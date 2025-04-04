sh.addShard("shardReplSet/mongo-shard-0.mongo-shard.mongo.svc.cluster.local:27018,mongo-shard-1.mongo-shard.mongo.svc.cluster.local:27018")
sh.enableSharding("RepoUser")
sh.shardCollection("RepoUser.UserModel", { "_id" : "hashed" } )
