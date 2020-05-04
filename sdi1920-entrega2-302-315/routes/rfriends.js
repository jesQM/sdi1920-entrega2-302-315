module.exports = function(app, swig, gestorBD) {

    app.get("/friends/list", function(req, res) {
        res.send("lista de amigos");
    });

    app.get("/friends/request", function(req, res) {
        res.send("see friend requests");
    });

    app.get("/friends/request/send/:id", function(req, res) {
        res.send("send friend request");
    });

    app.get("/friends/request/accept/:id", function(req, res) {
        res.send("accept friend request");
    });
};