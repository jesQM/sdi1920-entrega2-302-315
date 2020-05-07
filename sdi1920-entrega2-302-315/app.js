let express = require("express");
let app = express();

let expressSession = require('express-session');
app.use(expressSession({
    secret: 'abcdefg',
    resave: true,
    saveUninitialized: true
}));
let crypto = require('crypto');
let mongo = require('mongodb');
let swig = require('swig');
let bodyParser = require('body-parser');
let gestorBD = require("./modules/gestorBD.js");
gestorBD.init(app,mongo);
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
var jwt = require('jsonwebtoken');
app.set('jwt',jwt);

app.use(express.static('public'));
app.set('port', 8081);
app.set('db','mongodb://admin:sdi_Iz9@tiendamusica-shard-00-00-xmbgc.mongodb.net:27017,tiendamusica-shard-00-01-xmbgc.mongodb.net:27017,tiendamusica-shard-00-02-xmbgc.mongodb.net:27017/test?ssl=true&replicaSet=tiendamusica-shard-0&authSource=admin&retryWrites=true&w=majority');
app.set('clave','abcdefg');
app.set('crypto',crypto);

// ROUTERS \\
var routerUsuarioToken = express.Router();
routerUsuarioToken.use(function(req, res, next) {
    // obtener el token, vía headers (opcionalmente GET y/o POST).
    var token = req.headers['token'] || req.body.token || req.query.token;
    if (token != null) {
        // verificar el token
        jwt.verify(token, 'secreto', function(err, infoToken) {
            if (err || (Date.now()/1000 - infoToken.tiempo) > 240 ){
                res.status(403); // Forbidden
                res.json({
                    acceso : false,
                    error: 'Token invalido o caducado'
                });
                return;

            } else {
                res.usuario = infoToken.usuario;
                next();
            }
        });

    } else {
        res.status(403); // Forbidden
        res.json({
            acceso : false,
            mensaje: 'No hay Token'
        });
    }
});
app.use('/api/mensaje', routerUsuarioToken);
app.use('/api/amigos', routerUsuarioToken);

// ROUTES \\

require("./routes/rusuarios.js")(app, swig, gestorBD);
require("./routes/rfriends")(app, swig, gestorBD);
require("./routes/rapimensajes")(app, gestorBD);

app.get("/", function(req, res) {
    let respuesta = swig.renderFile('views/bhome.html', {user : req.session.usuario});
    res.send(respuesta);
});

app.listen(app.get('port'), function() {
    console.log('Server running at http://localhost:'+app.get('port')+'/');
});

gestorBD.obtenerUsuarios({}, usr => console.log(usr));

let friendship = {
    accepted : false,
    userTo : gestorBD.mongo.ObjectID("5eb0674de752915ec09db8ac"),
    userFrom : gestorBD.mongo.ObjectID("5eb082782062d137d4ebc43e"),
};
//gestorBD.insertarFriendship(friendship, usr => console.log(usr));