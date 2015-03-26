'use strict'
var net = require('net');
var events = require('events');
var util = require('util');
var informer = new events.EventEmitter(); //
informer.on("REGISTERED", register);
function PlayerServer() {
    this.server = net.createServer(function(player) {
        //TODO
    });
}
util.inherits(PlayerServer, events.EventEmitter);


PlayerServer.prototype.onInput = function(player, data) {
    //TODO
}


PlayerServer.prototype.listen = function(port) {
	this.server.listen(port, register);
	//TODO
}


PlayerServer.prototype.close = function() {
    //TODO
}


module.exports = PlayerServer;