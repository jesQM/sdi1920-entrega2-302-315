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

app.use(express.static('public'));
app.set('port', 8081);
app.set('db','mongodb://admin:sdi_Iz9@tiendamusica-shard-00-00-xmbgc.mongodb.net:27017,tiendamusica-shard-00-01-xmbgc.mongodb.net:27017,tiendamusica-shard-00-02-xmbgc.mongodb.net:27017/test?ssl=true&replicaSet=tiendamusica-shard-0&authSource=admin&retryWrites=true&w=majority');
app.set('clave','abcdefg');
app.set('crypto',crypto);

// ROUTERS \\


// ROUTES \\

require("./routes/rusuarios.js")(app, swig, gestorBD);
require("./routes/rfriends")(app, swig, gestorBD);

app.get("/", function(req, res) {
    let respuesta = swig.renderFile('views/bhome.html', {user : req.session.usuario});
    res.send(respuesta);
});

app.listen(app.get('port'), function() {
    console.log('Server running at http://localhost:'+app.get('port')+'/');
});