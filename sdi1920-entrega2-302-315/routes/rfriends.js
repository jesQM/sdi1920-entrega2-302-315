module.exports = function(app, swig, gestorBD) {

    app.get("/friends/list", function(req, res) {
        res.send("lista de amigos");
    });

    app.get("/friends/request", function(req, res) {
        res.send("see friend requests");
    });

    app.get("/friends/request/send/:id", function(req, res) {
        let criterio = {
            _id : gestorBD.mongo.ObjectID(req.params.id),
        };

        if (typeof req.session.usuario == "undefined" || req.session.usuario == null){
            res.send("Usuario no en sesión");
        } else {
            gestorBD.obtenerUsuarios(criterio, function (users) {
                if (!users || !users[0]) {
                    res.send("User not found");
                } else {
                    let userTo = users[0];
                    criterio = {
                        email : req.session.usuario,
                    };
                    gestorBD.obtenerUsuarios(criterio, function (users) {
                        if (!users || !users[0]) {
                            res.send("User not found");
                        } else {
                            let userFrom = users[0];
                            // Not sent to himself
                            if ( userFrom.email == userTo.email ){
                                res.send("Cannot befriend yourself");
                            } else {
                                let friendship = {
                                    accepted : false,
                                    userTo : userTo._id,
                                    userFrom : userFrom._id,
                                };
                                gestorBD.insertarFriendship(friendship, function (id) {
                                    if (!id) {
                                        res.send("There was an error adding");
                                    } else {
                                        res.redirect("/usuarios");
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    });

    app.get("/friends/request/accept/:id", function(req, res) {
        res.send("accept friend request");
    });
};