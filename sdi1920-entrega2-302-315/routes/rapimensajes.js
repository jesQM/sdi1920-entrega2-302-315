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
                    {usuario: criterio.email , tiempo: Date.now()/1000},
                    "secreto");
                res.status(200);
                res.json({
                    autenticado: true,
                    token : token
                });
            }

        });
    });

    app.post("/api/mensaje/crear", function(req, res) {
        var mensaje = {
            emisor : res.usuario.email,
            destino : req.body.destino, // email destino
            texto : req.body.texto,
            leido : false,
        };

        // 1.- Check they are friends
        let criterio = {
            userFrom: mensaje.emisor, userTo: mensaje.destino, accepted: true,
        };
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
        })

    });


    app.post("/api/mensaje/ver", function(req, res) {
        var conversacion = {
            $or : [
                { emisor : res.usuario.email, destino : req.body.destino },
                { emisor : req.body.destino, destino : res.usuario.email },
            ]
        };

        gestorBD.obtenerMensajes(conversacion, (msgs) => {
            if (msgs) {
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
}