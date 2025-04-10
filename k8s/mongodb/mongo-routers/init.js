// Add additional shards (only if not already added)
try {
    sh.addShard("shardReplSet/mongo-shard-0.mongo-shard.mongo.svc.cluster.local:27018,mongo-shard-1.mongo-shard.mongo.svc.cluster.local:27018,mongo-shard-2.mongo-shard.mongo.svc.cluster.local:27018");
} catch (e) {
    print("Shard already added or error:", e.message);
}

// === REPO USER SETUP ===
let repoUserDb = db.getSiblingDB("RepoUser");

if (!repoUserDb.getUser("admin")) {
    repoUserDb.createUser({
        user: "admin",
        pwd: "admin",
        roles: [{ role: "readWrite", db: "RepoUser" }]
    });
}

if (!repoUserDb.getCollectionNames().includes("UserModel")) {
    repoUserDb.createCollection("UserModel");
}

try {
    sh.enableSharding("RepoUser");
} catch (e) {
    print("Sharding already enabled or error:", e.message);
}

try {
    sh.shardCollection("RepoUser.UserModel", { "_id": "hashed" });
} catch (e) {
    print("Collection already sharded or error:", e.message);
}

// === REPO POST SETUP ===
let repoPostDb = db.getSiblingDB("RepoPost");

if (!repoPostDb.getUser("admin")) {
    repoPostDb.createUser({
        user: "admin",
        pwd: "admin",
        roles: [{ role: "readWrite", db: "RepoPost" }]
    });
}

if (!repoPostDb.getCollectionNames().includes("PostModel")) {
    repoPostDb.createCollection("PostModel");
}

try {
    sh.enableSharding("RepoPost");
} catch (e) {
    print("Sharding already enabled or error:", e.message);
}

try {
    sh.shardCollection("RepoPost.PostModel", { "_id": "hashed" });
} catch (e) {
    print("Collection already sharded or error:", e.message);
}

// === SRVC SEARCH SETUP ===
let searchDb = db.getSiblingDB("SrvcSearch");

if (!searchDb.getUser("admin")) {
    searchDb.createUser({
        user: "admin",
        pwd: "admin",
        roles: [{ role: "readWrite", db: "SrvcSearch" }]
    });
}

if (!searchDb.getCollectionNames().includes("SearchModel")) {
    searchDb.createCollection("SearchModel");
}

try {
    sh.enableSharding("SrvcSearch");
} catch (e) {
    print("Sharding already enabled or error:", e.message);
}

try {
    sh.shardCollection("SrvcSearch.SearchModel", { "_id": "hashed" });
} catch (e) {
    print("Collection already sharded or error:", e.message);
}

// === SRVC USER TIMELINE SETUP ===
let srvcUserDb = db.getSiblingDB("SrvcUserTimeline");

if (!searchDb.getUser("admin")) {
    searchDb.createUser({
        user: "admin",
        pwd: "admin",
        roles: [{ role: "readWrite", db: "SrvcUserTimeline" }]
    });
}

if (!srvcUserDb.getCollectionNames().includes("UserTimelinePost")) {
    srvcUserDb.createCollection("UserTimelinePost");
}

try {
    sh.enableSharding("SrvcUserTimeline");
} catch (e) {
    print("Sharding already enabled or error:", e.message);
}

try {
    sh.shardCollection("SrvcUserTimeline.UserTimelinePost", { "_id": "hashed" });
} catch (e) {
    print("Collection already sharded or error:", e.message);
}
