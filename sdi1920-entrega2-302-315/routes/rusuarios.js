module.exports = function (app, swig, gestorBD) {

    app.get("/usuarios", function (req, res) {
        let criterio = {};

        if (req.query.busqueda != null) {
            criterio = {
                $or: [{"name": {$regex: ".*" + req.query.busqueda + ".*", $options: 'i'}},
                    {"surname": {$regex: ".*" + req.query.busqueda + ".*", $options: 'i'}},
                    {"email": {$regex: ".*" + req.query.busqueda + ".*", $options: 'i'}}]
            };
        }

        let pg = parseInt(req.query.pg); // Es un String
        if (req.query.pg == null) { // Puede no venir el param
            pg = 1;
        }
        gestorBD.obtenerUsuariosPag(criterio, pg, function (usuarios, total) {
            if (usuarios == null) {
                res.send("Error al listar");
            } else {
                let ultimaPg = total / 4;
                if (total % 4 > 0) { // Sobran decimales
                    ultimaPg = ultimaPg + 1;
                }
                let paginas = []; // mostrar paginas
                for (let i = pg - 2; i <= pg + 2; i++) {
                    if (i > 0 && i <= ultimaPg) {
                        paginas.push(i);
                    }
                }
                let respuesta = swig.renderFile('views/busuarios.html', {
                    usuarios: usuarios,
                    paginas: paginas,
                    actual: pg
                });
                res.send(respuesta);
            }
        });
    });

    app.get("/registrarse", function (req, res) {
        let respuesta = swig.renderFile('views/bregistro.html', {});
        res.send(respuesta);
    });

    app.post('/usuario', function (req, res) {

        if (req.body.password !== req.body.passwordRepeat) {
            res.redirect("/registrarse?mensaje=Las contraseñas no coinciden&tipoMensaje=alert-danger");
            return;
        }

        let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let usuario = {
            name: req.body.name,
            surname: req.body.surname,
            email: req.body.email,
            password: seguro
        };

        let checkExistingEmail = {
            email: usuario.email
        };

        gestorBD.obtenerUsuarios(checkExistingEmail, function (usuarios) {
            if (usuarios === null || usuarios.length === 0) {
                gestorBD.insertarUsuario(usuario, function (id) {
                    if (id == null) {
                        res.redirect("/identificarse?mensaje=Error al registrar usuario");
                    } else {
                        res.redirect("/identificarse?mensaje=Nuevo usuario registrado");
                    }
                });
            } else {
                res.redirect(`/registrarse?mensaje=Ya existe un usuario con el email: ${checkExistingEmail.email} &tipoMensaje=alert-danger`)
            }

        });
    });

    app.get("/identificarse", function (req, res) {
        let respuesta = swig.renderFile('views/bidentificacion.html', {});
        res.send(respuesta);
    });

    app.post("/identificarse", function (req, res) {
        let seguro = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let criterio = {
            email: req.body.email,
            password: seguro
        }
        gestorBD.obtenerUsuarios(criterio, function (usuarios) {
            if (usuarios == null || usuarios.length == 0) {
                req.session.usuario = null;
                res.redirect("/identificarse" +
                    "?mensaje=Email o password incorrecto" +
                    "&tipoMensaje=alert-danger ");
            } else {
                req.session.usuario = usuarios[0];
                res.redirect("/"); // /usuarios // TODO;
            }
        });
    });

    app.get('/desconectarse', function (req, res) {
        req.session.usuario = null;
        res.redirect("/identificarse");
    });

    app.get('/whoami', function (req, res) {
        if (req.session && req.session.usuario) {
            res.send(req.session.usuario.email);
        } else {
            res.send(null);
        }
    });
};