const dbName = process.env.SRVC_SEARCH_MONGODB_DATABASE || "SrvcSearch";
const collectionName = process.env.SRVC_SEARCH_MONGODB_COLLECTION || "SearchModel";

db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles: [
            {
                role: "readWrite",
                db: dbName
            }
        ]
    }
);
db.createCollection(collectionName);