db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles: [
            {
                role: "readWrite",
                db: "SrvcUserTimeline"
            }
        ]
    }
);
db.createCollection("UserTimelineModel");
