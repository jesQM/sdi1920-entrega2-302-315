let express = require("express");
let app = express();
app.set('port', 8081);

app.listen(app.get('port'), function() {
    console.log('Server running at http://localhost:'+app.get('port')+'/');
});