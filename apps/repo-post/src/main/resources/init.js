db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles: [
            {
                role: "readWrite",
                db: "RepoPost"
            }
        ]
    }
);
db.createCollection("PostModel");
