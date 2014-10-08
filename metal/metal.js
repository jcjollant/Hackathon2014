var express = require('express');
var http = require('http');
var bodyParser = require('body-parser');
var fs = require('fs');

var app = express();
var router = express.Router();

// parse application/json
app.use(bodyParser.json());

// router
router.post('/sendorder/:place', function(req, res){
	// res.json('{"place":"'+ req.params.place +'", "post":"' + JSON.stringify(req.body, null, 2) + '"}');
	console.log("Received ("+req.path+"): " + JSON.stringify(req.body, null, 2));
  
	var nativeMessage = meEngine(req.body, req.params.place, 'NewOrderSingle');
    meRouteMessage(nativeMessage, req.params.place, function (nativeStatus) {
		var status = meEngine(nativeStatus, req.params.place, 'ExecutionReport');
		res.json(status);	
	});

});

router.get('/grammar/:id', function(req, res){
  // res.json('{"place":"'+ req.params.place +'", "post":"' + JSON.stringify(req.body, null, 2) + '"}');
  console.log("Received ("+req.path+"): " + grammars[req.params.id]);
  res.json(JSON.stringify(grammars[req.params.id], null, 2));
  
  
/*  var nativeMessage = meEngine(req.body, req.params.place, 'order');
  meRouteMessage(nativeMessage, req.params.place, function (nativeStatus) {
	var status = meEngine(nativeStatus, req.params.place, 'status');
	res.json(status);	
  });
*/
});

router.use(express.static('static'));

app.use('/metal', router);

// start server
var server = app.listen(81, function() {
    console.log('<=METAL=> listening on port %d', server.address().port);
});

// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function meEngine(message, place, type) {
	var transformer = getTransformer(place, type);
	var result = transformer.process(message);
    return result;
}

var transformers = {};

function getTransformer(place, type) {

	// get transformer
	var grammar = grammars[place];
	var messageGrammar = grammar.Messages[type];

	messageGrammar.endianness = grammar.properties.endianness;
	messageGrammar.mappingTables = grammar.mappingTables;
	/*if(transformers.hasOwnProperty(place)){
		if(transformers[place].hasOwnProperty(type))
	}*/
	var transformer = buildTransformer(messageGrammar);
	return transformer;
}

function buildTransformer(messageGrammar) {
	//build transformer (le gras/the fat)
	var transformer  = messageGrammar.fixMessage == "NewOrderSingle" ? 
		new TransformerFixToNative(messageGrammar) : new TransformerNativeToFix(messageGrammar);
	return transformer;
}

function TransformerFixToNative(messageGrammar){
	var self = this;
	self.length = messageGrammar.length;
	self.buffer = new Buffer(self.length);
	
	function copyFrom(originTag){
		if(self.message.hasOwnProperty(originTag)){
			return self.message[originTag];
		}
		console.log("Tag " + originTag + " not found in original message!");
	}
	
	function mappingFrom(mapName, originTag){
		if(self.message.hasOwnProperty(originTag)){
			debugger;
			var values = messageGrammar.mappingTables[mapName].values;
			/*for(var i=0; i < values.length; i++){
				if(values[i].FIX == self.message[originTag]){
					return values[i]['native'];
				}
			}*/
			
			for(var props in values){
				if(values[props].FIX == self.message[originTag]){
					return values[props]['native'];
				}
			}
			//return messageGrammar.mappingTables[mapName].values[self.message[originTag]]["native"];
		}
		console.log("Tag " + originTag + " not found in original message!");
	}
	
	self.process = function(message){
		self.message = message;
		self.buffer.fill(0);	
		for(var i=0; i< messageGrammar.fields.length; i++){			
			var field = messageGrammar.fields[i];
			var value = eval(field.value); 
			if(value){
				writeInBuffer(field, value);
			}
		}
		return self.buffer;
	};	
	
	function writeInBuffer(field, value){
		switch (field.type){
			case "uint":
			case "price":
				if(field.size == 1){
					self.buffer.writeUInt8(value, field.pos);
				}
				else if(field.size == 2){
					if(messageGrammar.endianness == "little"){
						self.buffer.writeUInt16LE(value, field.pos);
					}
					else{
						self.buffer.writeUInt16BE(value, field.pos);
					}
				} 
				else if(field.size == 4){
					if(messageGrammar.endianness == "little"){
						self.buffer.writeUInt32LE(value, field.pos);
					}
					else{
						self.buffer.writeUInt32BE(value, field.pos);
					}
				}
				else if(field.size == 8){
					if(messageGrammar.endianness == "little"){
						self.buffer.writeUInt32LE(value, field.pos);
						self.buffer.writeUInt32LE(0, field.pos + 4);
					}
					else{
						self.buffer.writeUInt32BE(0, field.pos);
						self.buffer.writeUInt32BE(value, field.pos);
					}
				}
				else {
					console.log("UInt field size not managed!");
				}
				break;
			case "int":
				if(field.size == 1){
					self.buffer.writeInt8(value, field.pos);
				}
				else if(field.size == 2){
					if(messageGrammar.endianness == "little"){
						self.buffer.writeInt16LE(value, field.pos);
					}
					else{
						self.buffer.writeInt16BE(value, field.pos);
					}
				} 
				else if(field.size == 4){
					if(messageGrammar.endianness == "little"){
						self.buffer.writeInt32LE(value, field.pos);
					}
					else{
						self.buffer.writeInt32BE(value, field.pos);
					}
				}
				else if(field.size == 8){
					if(messageGrammar.endianness == "little"){
						self.buffer.writeInt32LE(value, field.pos);
						self.buffer.writeInt32LE(0, field.pos + 4);
					}
					else{
						self.buffer.writeInt32BE(0, field.pos);
						self.buffer.writeInt32BE(value, field.pos);
					}
				}
				
				else {
					console.log("Int field size not managed!");
				}
				break;
			case "string":
				debugger;
				self.buffer.write(value, field.pos, field.size);
				break;
			default:
				console.log('Field type not defined !');
		}
	}
}

function TransformerNativeToFix(messageGrammar){
	var self = this;
	self.length = messageGrammar.length;
	self.message = {};
	self.cvalue;
	
	function copyTo(tag){
		self.message[tag] = self.cvalue;	
		console.log("Tag " + tag + " created with " + self.cvalue);
	}
	
	function setSecurityID(tag){
		self.message['SecurityID'] = self.cvalue;
		self.message['SecurityIDSource'] = tag;	
		console.log("Tag " + tag + " created with " + self.cvalue);
	}
	
	function setTransactTimeMSSM(tag){
		self.message[tag] = self.cvalue;	
		console.log("Tag " + tag + " created with " + self.cvalue);
	}
	
	function mappingFrom(mapName, originTag){
		if(self.message.hasOwnProperty(originTag)){
			debugger;
			var values = messageGrammar.mappingTables[mapName].values;
			/*for(var i=0; i < values.length; i++){
				if(values[i].FIX == self.message[originTag]){
					return values[i]['native'];
				}
			}*/
			
			for(var props in values){
				if(values[props].FIX == self.message[originTag]){
					return values[props]['native'];
				}
			}
			//return messageGrammar.mappingTables[mapName].values[self.message[originTag]]["native"];
		}
		console.log("Tag " + originTag + " not found in original message!");
	}
	
	self.process = function(buffer){
		// buffer is a Buffer!
		self.buffer = buffer;
	
		for(var i=0; i< messageGrammar.fields.length; i++){			
			var field = messageGrammar.fields[i];
			self.cvalue = readFromBuffer(field);
			eval(field.value);
		}
		return self.message;
	};	
	
	function readFromBuffer(field){
		switch (field.type){
			case "uint":
			case "price":
				if(field.size == 1){
					return self.buffer.readUInt8(field.pos);
				}
				else if(field.size == 2){
					if(messageGrammar.endianness == "little"){
						return self.buffer.readUInt16LE(field.pos);
					}
					else{
						return self.buffer.readUInt16BE(field.pos);
					}
				} 
				else if(field.size == 4){
					if(messageGrammar.endianness == "little"){
						return self.buffer.readUInt32LE(field.pos);
					}
					else{
						return self.buffer.readUInt32BE(field.pos);
					}
				}
				else if(field.size == 8){
					if(messageGrammar.endianness == "little"){
						return self.buffer.readUInt32LE(field.pos);
						//self.buffer.readUInt32LE(0, field.pos + 4);
					}
					else{
						//self.buffer.readUInt32BE(0, field.pos);
						return self.buffer.readUInt32BE(field.pos);
					}
				}
				else {
					console.log("UInt field size not managed!");
				}
				break;
			case "int":
				if(field.size == 1){
					return self.buffer.readInt8(field.pos);
				}
				else if(field.size == 2){
					if(messageGrammar.endianness == "little"){
						return self.buffer.readInt16LE(field.pos);
					}
					else{
						return self.buffer.readInt16BE(field.pos);
					}
				} 
				else if(field.size == 4){
					if(messageGrammar.endianness == "little"){
						return self.buffer.readInt32LE(field.pos);
					}
					else{
						return self.buffer.readInt32BE(field.pos);
					}
				}
				else if(field.size == 8){
					if(messageGrammar.endianness == "little"){
						return self.buffer.readInt32LE(field.pos);
						// self.buffer.writeInt32LE(0, field.pos + 4);
					}
					else{
						//self.buffer.readInt32BE(0, field.pos);
						return self.buffer.readInt32BE(field.pos);
					}
				}
				
				else {
					console.log("Int field size not managed!");
				}
				break;
			case "string":
				return self.buffer.toString('utf8', field.pos, field.pos + field.size).trim();
				break;
			default:
				console.log('Field type not defined !');
		}
	}
}

// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

var optionsLSE = {
  host: '10.25.101.70',
//  host: '10.25.101.62',
  port: 10003
};
var optionsNYSE = {
  host: '10.25.101.70',
//  host: '10.25.101.62',
  port: 10004
};

var net = require('net');
/*
var clientNYSE = net.connect(optionsNYSE,
	function() { //'connect' listener
		console.log('client NYSE connected');
});
*/
function meRouteMessage(message, place, callback) {
	var options = place == "lse" ? optionsLSE : optionsNYSE;
	
	var client = net.connect(options,
		function() { //'connect' listener
			console.log('client connected, sends: ' + message.toString());
			client.write(message);
	});
	
	client.on('data', function(data) {
	  console.log('Client received: ' + data.toString());
	  callback(data);
	  console.log('Trying to close connection');
	  client.end();
	});
	client.on('end', function() {
	  console.log('Client disconnected');
	});
};

/*
function meRouteMessage(message, place, callback) {

	var req = http.request(options, function(res) {
	  console.log('STATUS: ' + res.statusCode);
	  console.log('HEADERS: ' + JSON.stringify(res.headers));
	  res.setEncoding('utf8');
	  res.on('data', function (chunk) {
		console.log('BODY: ' + chunk);
		callback(chunk);
	  });
	});

	req.on('error', function(e) {
	  console.log('problem with request: ' + e.message);
	});

	// write data to request body
	debugger;
	req.write(message);
	req.end();
};
*/

var lse = 
{       "properties": 
	{       "endianness": "little",
		"name": "LSE Trading",
		"specVersion": "Native Trading Gateway Issue 11.1",
		"namespace": "LSE",
		"uuid" : "874bd640-47e3-11e4-916c-0800200c9a66"
	},
        "Messages": {
		"NewOrderSingle" :
		{       "name": "NewOrderSingle",
			"nativeMessageType": "D",
			"fixMessage": "NewOrderSingle",
			"length" : 97,
			"fields":[
			{       "name": "ClientOrderID",
				"pos": 4,
				"type": "string",
				"size" : 20,
				"value": "copyFrom('ClOrdID')"
			},
			{       "name": "TraderID",
				"pos": 24,
				"type": "string",
				"size" : 11,
				"value": "'0'"
			},
			{       "name": "Account",
				"pos": 35,
				"type": "string",
				"size" : 10,
				"value": "copyFrom('Account')"
			},
			{       "name": "ClearingAccount",
				"pos": 45,
				"type": "uint",
				"size" : 1,
				"value": 1
			},
			{       "name": "Instrument ID",
				"pos": 46,
				"type": "int",
				"size" : 4,
				"value": 12
			},
			{       "name": "ReservedField",
				"pos": 50,
				"type": "int",
				"size" : 1,
				"value": 0
			},
			{       "name": "ReservedField",
				"pos": 51,
				"type": "int",
				"size" : 1,
				"value": 0
			},
			{       "name": "OrderType",
				"pos": 52,
				"type": "uint",
				"size" : 1,
				"value": "copyFrom('OrdType')"
			},
			{       "name": "TIF",
				"pos": 53,
				"type": "uint",
				"size" : 1,
				"value": 0
			},
			{       "name": "ExpireDateTime",
				"pos": 54,
				"type": "uint",
				"size" : 4,
				"value": "copyFrom('ExpireTime')"
			},
			{       "name": "Side",
				"pos": 58,
				"type": "uint",
				"size" : 1,
				"value": "copyFrom('Side')"
			},
			{       "name": "OrderQty",
				"pos": 59,
				"type": "int",
				"size" : 4,
				"value": "copyFrom('OrderQty')"
			},
			{       "name": "DisplayQty",
				"pos": 63,
				"type": "int",
				"size" : 4,
				"value": "copyFrom('DisplayQty')"
			},
			{       "name": "LimitPrice",
				"pos": 67,
				"type": "int",
				"size" : 8,
				"value": "copyFrom('Price')"
			},
			{       "name": "Capacity",
				"pos": 75,
				"type": "uint",
				"size" : 1,
				"value": "mappingFrom('CapacityOfOrder','OrderCapacity')"
			},
			{       "name": "AutoCancel",
				"pos": 76,
				"type": "uint",
				"size" : 1,
				"value": 0
			},
			{       "name": "OrderSubType",
				"pos": 77,
				"type": "uint",
				"size" : 1,
				"value": 0
			},
			{       "name": "Anonymity",
				"pos": 78,
				"type": "uint",
				"size" : 1,
				"value": "copyFrom('PreTradeAnonymity')"
			},
			{       "name": "StoppedPrice",
				"pos": 79,
				"type": "price",
				"size" : 8,
				"value": 0
			},
			{       "name": "PassiveOnlyOrder",
				"pos": 87,
				"type": "uint",
				"size" : 1,
				"value": 0
			},
			{       "name": "ReservedField",
				"pos": 88,
				"type": "string",
				"size" : 9,
				"value": "'0'"
			}
			]
		},
		"ExecutionReport" :
		{	"name": "ExecutionReport",
			"nativeMessageType": "8",
			"fixMessage": "ExecutionReport",
			"length" : 151,
			"fields":[
			{	"name": "AppID",
				"pos": 4,
				"type": "uint",
				"size" : 1,
				"value": "copyTo('ApplID')"
			},
			{	"name": "SequenceNo",
				"pos": 5,
				"type": "int",
				"size" : 4,
				"value": "copyTo('ApplSeqNum')"
			},
			{	"name": "ExecutionID",
				"pos": 9,
				"type": "string",
				"size" : 12,
				"value": "copyTo('ExecID')"
			},
			{	"name": "ClientOrderID",
				"pos": 21,
				"type": "string",
				"size" : 20,
				"value": "copyTo('ClOrdID')"
			},
			{	"name": "OrderID",
				"pos": 41,
				"type": "string",
				"size" : 12,
				"value": "copyTo('OrderID')"
			},
			{	"name": "ExecType",
				"pos": 53,
				"type": "string",
				"size" : 1,
				"value": "copyTo('ExecType')"
			},
			{	"name": "OrderStatus",
				"pos": 66,
				"type": "int",
				"size" : 1,
				"value": "copyTo('OrdStatus')"
			},
			{	"name": "ExecutedQty",
				"pos": 79,
				"type": "int",
				"size" : 4,
				"value": "copyTo('CumQty')"
			},
			{	"name": "LeavesQty",
				"pos": 83,
				"type": "int",
				"size" : 4,
				"value": "copyTo('LeavesQty')"
			},
			{	"name": "InstrumentID",
				"pos": 92,
				"type": "int",
				"size" : 4,
				"value": "setSecurityID('M')"
			},
			{	"name": "Side",
				"pos": 98,
				"type": "int",
				"size" : 1,
				"value": "copyTo('Side')"
			}
			]
	}},
	"mappingTables": {
		"CapacityOfOrder" : {	
			"type": "uint",
			"size": 1,
			"values": {
				"RisklessPrincipal": {"native":"1", "FIX":"I"},
				"Principal": {"native":"2", "FIX":"P"},
				"Agency": {"native":"3", "FIX":"A"},
				"CFDGiveUp": {"native":"4", "FIX":"G"}
			}
		}
		}
}
;

var nyse =
{       "properties": 
	{       "endianness": "big",
		"name": "NYSE UTP Direct",
		"specVersion": "NYSE UTP Direct API Specification v1.18",
		"namespace": "NYSE",
		"uuid" : "7ae53ea0-4946-11e4-916c-0800200c9a66"
	},
        "Messages": {
		"ExecutionReport" :
		{       "name": "OrderFill",
			"nativeMessageType": "0x0081",
			"fixMessage": "ExecutionReport",
			"length" : 116,
			"fields":[
			{       "name": "MessageType",
				"pos": 0,
				"type": "int",
				"size" : 2,
				"value": 0
			},
			{       "name": "MsgLength",
				"pos": 2,
				"type": "int",
				"size" : 2,
				"value": 116
			},
			{       "name": "MsgSeqNum",
				"pos": 4,
				"type": "int",
				"size" : 4,
				"value": 0
			},
			{       "name": "MEOrderID",
				"pos": 8,
				"type": "int",
				"size" : 4,
				"value": "copyTo('OrderID')"
			},
			{       "name": "TransactTime",
				"pos": 12,
				"type": "int",
				"size" : 4,
				"value": "setTransactTimeMSSM('TransactTime')"
			},
			{       "name": "LeavesQty",
				"pos": 16,
				"type": "int",
				"size" : 4,
				"value": "copyTo('LeavesQty')"
			},
			{       "name": "LastShares",
				"pos": 20,
				"type": "int",
				"size" : 4,
				"value": "copyTo('LastQty')"
			},
			{       "name": "LastPrice",
				"pos": 24,
				"type": "int",
				"size" : 4,
				"value": "copyTo('LastPx')"
			},
			{       "name": "PriceScale",
				"pos": 28,
				"type": "string",
				"size" : 1,
				"value": "0"
			},
			{       "name": "Side",
				"pos": 29,
				"type": "string",
				"size" : 1,
				"value": "copyTo('Side')"
			},
			{       "name": "BillingIndicator",
				"pos": 30,
				"type": "string",
				"size" : 1,
				"value": "'1'"
			},
			{       "name": "LastMarket",
				"pos": 31,
				"type": "int",
				"size" : 1,
				"value": "copyTo('LastMkt')"
			},
			{       "name": "DeliverToCompID",
				"pos": 32,
				"type": "string",
				"size" : 5,
				"value": "copyTo('OnBehalfOfCompID')"
			},
			{       "name": "TargetSubID",
				"pos": 37,
				"type": "string",
				"size" : 5,
				"value": "copyTo('SenderSubID')"
			},
			{       "name": "ExecBroker",
				"pos": 42,
				"type": "string",
				"size" : 5,
				"value": ""
			},
			{       "name": "ContraBroker",
				"pos": 47,
				"type": "string",
				"size" : 5,
				"value": "copyTo('ContraBroker')"
			},
			{       "name": "ContraTrader",
				"pos": 52,
				"type": "string",
				"size" : 5,
				"value": "copyTo('ContraTrader')"
			},
			{       "name": "ExecAwayMktID",
				"pos": 57,
				"type": "string",
				"size" : 6,
				"value": ""
			},
			{       "name": "BillingRate",
				"pos": 63,
				"type": "string",
				"size" : 6,
				"value": ""
			},
			{       "name": "ExecID",
				"pos": 69,
				"type": "string",
				"size" : 10,
				"value": "copyTo('ExecID')"
			},
			{       "name": "Account",
				"pos": 79,
				"type": "string",
				"size" : 10,
				"value": "copyTo('Account')"
			},
			{       "name": "DBExecID",
				"pos": 89,
				"type": "string",
				"size" : 10,
				"value": ""
			},
			{       "name": "ClientOrderID",
				"pos": 99,
				"type": "string",
				"size" : 17,
				"value": "copyTo('ClOrdID')"
			}
			]
		},
		"NewOrderSingle" :
		{	"name": "NewOrderSingle",
			"nativeMessageType": "0x0041",
			"fixMessage": "NewOrderSingle",
			"length": 84,
			"fields":[
			{	"name": "MessageType",
				"pos": 0,
				"type": "int",
				"size" : 2,
				"value": 0
			},
			{	"name": "MsgLength",
				"pos": 2,
				"type": "int",
				"size" : 2,
				"value": 0
			},
			{	"name": "MsgSeqNum",
				"pos": 4,
				"type": "int",
				"size" : 4,
				"value": 0
			},
			{	"name": "OrderQty",
				"pos": 8,
				"type": "int",
				"size" : 4,
				"value": "copyFrom('OrderQty')"
			},
			{	"name": "MaxFloorQty",
				"pos": 12,
				"type": "int",
				"size" : 4,
				"value": 0
			},
			{	"name": "Price",
				"pos": 16,
				"type": "int",
				"size" : 4,
				"value": "copyFrom('Price')"
			},
			{	"name": "PriceScale",
				"pos": 20,
				"type": "string",
				"size" : 1,
				"value": "'0'"
			},
			{	"name": "Symbol",
				"pos": 21,
				"type": "string",
				"size" : 11,
				"value": "copyFrom('Symbol')"
			},
			{	"name": "ExecInst",
				"pos": 32,
				"type": "string",
				"size" : 1,
				"value": "copyFrom('ExecInst')"
			},
			{	"name": "Side",
				"pos": 33,
				"type": "string",
				"size" : 1,
				"value": "copyFrom('Side')"
			},
			{	"name": "OrderType",
				"pos": 34,
				"type": "string",
				"size" : 1,
				"value": "copyFrom('OrdType')"
			},
			{	"name": "TimeInForce",
				"pos": 35,
				"type": "string",
				"size" : 1,
				"value": "'0'"
			},
			{	"name": "OrderCapacity",
				"pos": 36,
				"type": "string",
				"size" : 1,
				"value": "copyFrom('OrderCapacity')"
			},
			{	"name": "RoutingInstruction",
				"pos": 37,
				"type": "string",
				"size" : 1,
				"value": "'I'"
			},
			{	"name": "DOTReserve",
				"pos": 38,
				"type": "string",
				"size" : 1,
				"value": "'N'"
			},
			{	"name": "OnBehalfOfCompID",
				"pos": 39,
				"type": "string",
				"size" : 5,
				"value": "copyFrom('OnBehalfOfCompID')"
			},
			{	"name": "SenderSubID",
				"pos": 44,
				"type": "string",
				"size" : 5,
				"value": "copyFrom('SenderSubID')"
			},
			{	"name": "ClearingFirm",
				"pos": 49,
				"type": "string",
				"size" : 5,
				"value": "''"
			},
			{	"name": "Account",
				"pos": 54,
				"type": "string",
				"size" : 10,
				"value": "copyFrom('Account')"
			},
			{	"name": "ClientOrderID",
				"pos": 64,
				"type": "string",
				"size" : 17,
				"value": "copyFrom('ClOrdID')"
			},
			{	"name": "Filler",
				"pos": 81,
				"type": "string",
				"size" : 3,
				"value": "''"
			}
			]
	}}
}
;

var grammars = 
{
	"lse" : lse,
	"nyse" : nyse
};