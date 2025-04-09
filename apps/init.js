const username = "admin";
const password = "admin";

const dbConfigs = [
    {
        name: "RepoUser",
        collections: ["UserModel"]
    },
    {
        name: "RepoPost",
        collections: ["PostModel"]
    },
    {
        name: "SrvcSearch",
        collections: ["SearchModel"]
    }
];

dbConfigs.forEach(config => {
    const dbRef = db.getSiblingDB(config.name);

    dbRef.createUser({
        user: username,
        pwd: password,
        roles: [{ role: "readWrite", db: config.name }]
    });

    config.collections.forEach(collectionName => {
        dbRef.createCollection(collectionName);
    });
});
