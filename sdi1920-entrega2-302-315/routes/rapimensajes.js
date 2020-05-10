module.exports = function(app, gestorBD) {

    app.post("/api/autenticar/", function(req, res) {
        var seguro = app.get("crypto")
            .createHmac('sha256', app.get('clave'))
            .update(req.body.password)
            .digest('hex');

        var criterio = {
            email : req.body.email,
            password : seguro
        };

        gestorBD.obtenerUsuarios(criterio, function(usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                res.status(401);
                res.json({
                    autenticado : false
                })
            } else {
                var token = app.get('jwt').sign(
                    {usuario: usuarios[0] , tiempo: Date.now()/1000},
                    "secreto");
                res.status(200);
                res.json({
                    autenticado: true,
                    token : token,
                    email : usuarios[0].email
                });
            }

        });
    });

    app.get("/api/amigos", function(req, res) {
        let criterio = {
            userFrom: gestorBD.mongo.ObjectID(res.usuario._id.toString()),
            accepted: true,
        };
        gestorBD.obtenerFriendship( criterio, (fr) => {
            if (fr) {
                criterio = {
                    $or : fr.map( (friendship) => {return { _id : gestorBD.mongo.ObjectID(friendship.userTo.toString())}} )
                }
                gestorBD.obtenerUsuarios(criterio, (amigos) => {
                    if (amigos) {
                        res.status(200);
                        res.json(JSON.stringify(amigos));
                    } else {
                        res.status(500);
                        res.json({
                            error : "No se han podido cargar los amigos"
                        });
                    }
                });
            } else {
                res.status(500);
                res.json({
                    error : "No se han podido cargar los amigos"
                });
            }
        })

    });

    app.post("/api/mensaje/crear", function(req, res) {
        var mensaje = {
            emisor : res.usuario.email,
            destino : req.body.destino, // email destino
            texto : req.body.texto,
            leido : false,
        };

        let criterio = { $or : [
                {email: mensaje.emisor},
                {email: mensaje.destino}
            ]
        };
        // 1.- Get the ids
        gestorBD.obtenerUsuarios(criterio, (users) => {

            if (users) {
                if (users.length != 2) {
                    res.status(400);
                    res.json({
                        error : "No se pudo encontrar al usuario"
                    });
                } else {
                    criterio = {
                        userFrom: users[0]._id, userTo: users[1]._id, accepted: true,
                    };
                    // 2.- Check they are friends
                    gestorBD.obtenerFriendship( criterio, (fr) => {
                        if (fr && fr.length > 0) {
                            gestorBD.insertarMensaje(mensaje, (id) => {
                                if (id) {
                                    res.status(201);
                                    res.json({
                                        mensaje: "mensaje enviado",
                                        _id: id
                                    });
                                } else {
                                    res.status(500);
                                    res.json({
                                        error : "No se pudo crear el mensaje"
                                    });
                                }
                            });
                        } else {
                            res.status(400);
                            res.json({
                                error : "Solo puedes mandar mensajes a tus amigos"
                            });
                        }
                    });
                }
            } else {
                res.status(500);
                res.json({
                    error : "No se pudo encontrar al usuario"
                });
            }
        });
    });

    app.get("/api/mensaje/ver/:email", function(req, res) {
        var conversacion = {
            $or : [
                { emisor : res.usuario.email, destino : req.params.email },
                { emisor : req.params.email, destino : res.usuario.email },
            ]
        };

        console.log(conversacion);
        gestorBD.obtenerMensajes(conversacion, (msgs) => {
            if (msgs) {
                console.log(msgs);
                res.status(200);
                res.json(JSON.stringify(msgs));
            } else {
                res.status(500);
                res.json({
                    error : "No se pudo leer la conversación"
                });
            }
        });

    });

    app.post("/api/mensaje/numero", function(req, res) {

        var amigo = req.body.amigo;

        var criterio = {
            emisor: amigo.email,
            destino: res.usuario.email,
            leido: false,
        };

        gestorBD.obtenerNumeroMensajes(criterio, (numeroMensajes) => {
            console.log(numeroMensajes);
                res.status(200);
                amigo.numberOfMessages = numeroMensajes;
                res.json(JSON.stringify(amigo));
        });

    });
}