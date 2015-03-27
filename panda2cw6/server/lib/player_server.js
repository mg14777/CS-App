'use strict'
var net = require('net');
var events = require('events');
var util = require('util');


function PlayerServer() {
    this.server = net.createServer(function(player) {
    player.on('register', function(data) {
    	//var parts = data.toString().split('\n');
    	var message = JSON.parse(data);
    	if(message.type == 'REGISTER')
    		this.emit('register',message.student_ID);
    })	
        //TODO
    });
}
util.inherits(PlayerServer, events.EventEmitter);


PlayerServer.prototype.onInput = function(player, data) {
    //TODO
}


PlayerServer.prototype.listen = function(port) {
	this.server.listen(port);
	//TODO
}


PlayerServer.prototype.close = function() {
    //TODO
}


module.exports = PlayerServer;