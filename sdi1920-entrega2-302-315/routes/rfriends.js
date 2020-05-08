module.exports = function(app, swig, gestorBD) {

    app.get("/friends/list", function(req, res) {
        res.send("lista de amigos");
    });

    app.get("/friends/request", function(req, res) {
        let criterio = {
            email : req.session.usuario.email,
        };
        let pg = parseInt(req.query.pg); // Es String !!!
        if ( req.query.pg == null){ // Puede no venir el param
            pg = 1;
        }
        gestorBD.obtenerUsuarios( criterio, (users) => {
            if (users && users[0]){
                let myself = users[0];
                let criterio2 = {
                    userTo : myself._id,
                };
                gestorBD.obtenerFriendship(criterio2, (requests) => {
                    if (requests) {
                        getAllUsersFromIdPag( requests.filter( r => !r.accepted).map( r => r.userFrom ), pg ,(users, total) => {
                            if (users) {
                                let ultimaPg = total/5;
                                if (total % 5 > 0 ){ // Sobran decimales
                                    ultimaPg = ultimaPg+1;
                                }
                                let paginas = []; // paginas mostrar
                                for(let i = pg-2 ; i <= pg+2 ; i++){
                                    if ( i > 0 && i <= ultimaPg){
                                        paginas.push(i);
                                    }
                                }

                                let respuesta = swig.renderFile('views/bpeticionesAmistad.html', {
                                    users : users,
                                    paginas : paginas,
                                    actual : pg
                                });
                                res.send(respuesta);
                            } else {
                                res.send("Error getting users");
                            }
                        });
                    } else {
                        res.send("Error getting requests");
                    }
                });

            } else {
                res.send("Error getting user");
            }
        });
    });

    function getAllUsersFromIdPag( arrayOfIDs, pg, callback ){
        if (arrayOfIDs.length == 0) {
            callback([]);
        } else {
            let criterio = {
                $or : arrayOfIDs.map( (identifier) => {return { _id : gestorBD.mongo.ObjectID(identifier.toString())}} )
            }
            gestorBD.obtenerUsuariosPag(criterio, pg, callback);
        }
    }

    app.get("/friends/request/send/:id", function(req, res) {
        let checkForHexRegExp = new RegExp("^[0-9a-fA-F]{24}$");
        if (!checkForHexRegExp.test(req.params.id.toString())) {
            res.redirect("/usuarios" +
                "?mensaje=Usuario no encontrado"+
                "&tipoMensaje=alert-danger ");
        } else {
            let criterio = {
                _id: gestorBD.mongo.ObjectID(req.params.id),
            };

            if (typeof req.session.usuario == "undefined" || req.session.usuario == null) {
                res.send("Usuario no en sesión");
            } else {
                gestorBD.obtenerUsuarios(criterio, function (users) {
                    if (!users || !users[0]) {
                        res.redirect("/usuarios" +
                            "?mensaje=Usuario no encontrado"+
                            "&tipoMensaje=alert-danger ");
                    } else {
                        let userTo = users[0];
                        let userFrom = req.session.usuario;
                        // Not sent to himself
                        if (userFrom.email == userTo.email) {
                            res.redirect("/usuarios" +
                                "?mensaje=No puedes mandar una solicitud a ti mismo"+
                                "&tipoMensaje=alert-danger ");
                        } else {
                            let friendship = {
                                userTo: userTo._id,
                                userFrom: userFrom._id,
                            };
                            gestorBD.obtenerFriendship(friendship, (fr) => {
                                if (fr) {
                                    // Has been sent already or is friend?
                                    if (fr.length > 0) {
                                        if (fr[0].accepted) {
                                            res.redirect("/usuarios" +
                                                "?mensaje=¡Ya es tu amigo!" +
                                                "&tipoMensaje=alert-warning");
                                        } else {
                                            res.redirect("/usuarios" +
                                                "?mensaje=¡Ya ha sido enviada anteriormente!" +
                                                "&tipoMensaje=alert-warning");
                                        }
                                    } else {
                                        gestorBD.insertarFriendship(friendship, function (id) {
                                            if (!id) {
                                                res.send("There was an error adding");
                                            } else {
                                                res.redirect("/usuarios" +
                                                    "?mensaje=¡Petición Enviada!" +
                                                    "&tipoMensaje=alert-success");
                                            }
                                        });
                                    }
                                } else {
                                    res.send("Error interno");
                                }
                            });
                        }
                    }
                });
            }
        }
    });

    app.get("/friends/request/accept/:friendId", function(req, res) {
        let checkForHexRegExp = new RegExp("^[0-9a-fA-F]{24}$");
        if (!checkForHexRegExp.test(req.params.friendId.toString())) {
            res.redirect("/friends/request" +
                "?mensaje=Petición de amistad no encontrada"+
                "&tipoMensaje=alert-danger ");
        } else {
            let criterio = {
                userTo : gestorBD.mongo.ObjectID(req.session.usuario._id.toString()),
                userFrom : gestorBD.mongo.ObjectID(req.params.friendId.toString()),
            };
            // 1.- find request and mark as true
            gestorBD.obtenerFriendship( criterio, (friendships) => {
                if (friendships) {
                    if (friendships[0] && !friendships[0].accepted){
                        let fr = {
                            accepted : true,
                        };
                        gestorBD.modificarFriendship( { _id : friendships[0]._id},fr, (updated) => {
                            if (updated) {
                                // 2.- Create another in the other way as true
                                let friendship = {
                                    accepted : true,
                                    userFrom : gestorBD.mongo.ObjectID(req.session.usuario._id.toString()),
                                    userTo : gestorBD.mongo.ObjectID(req.params.friendId.toString()),
                                };
                                gestorBD.insertarFriendship(friendship, function (id) {
                                    if (!id) {
                                        res.send("There was an error adding");
                                    } else {
                                        res.redirect("/friends/request" +
                                            "?mensaje=¡Petición aceptada!"+
                                            "&tipoMensaje=alert-success ");
                                    }
                                });
                            } else {
                                res.send("Error updating friendship");
                            }
                        });

                    } else {
                        res.redirect("/friends/request" +
                            "?mensaje=Petición de amistad no encontrada"+
                            "&tipoMensaje=alert-danger ");
                    }
                } else {
                    res.send("Error getting friendship");
                }
            });
        }

    });
};