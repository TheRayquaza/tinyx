db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles: [
            {
                role: "readWrite",
                db: "SrvcHomeTimeline"
            }
        ]
    }
);
db.createCollection("HomeTimelineModel");

