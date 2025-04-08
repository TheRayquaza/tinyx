db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles: [
            {
                role: "readWrite",
                db: "SrvcSearch"
            }
        ]
    }
);
db.createCollection("SearchModel");
