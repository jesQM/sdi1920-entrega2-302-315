module.exports = function(app, gestorBD) {

    // pp.post("/api/autenticar/", ()=>{});
    // TODO; Que la variable usuario sea todo el objeto, para asi poder sacar el id o el nombre según se necesite
    // Igual que está en req.sesion.usuario

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