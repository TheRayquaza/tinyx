db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles: [
            {
                role: "readWrite",
                db: "RepoUser"
            }
        ]
    }
);
db.createCollection("UserModel");
