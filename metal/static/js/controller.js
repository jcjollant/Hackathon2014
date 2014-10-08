var metalApp = angular.module('metalApp', []);

metalApp.controller('MetalCtrl', function ($scope, $q, $http) {

	$scope.model = {
		OrderQty: "666",
		Price: "500",
		Symbol: "F (Ford Motor Co)",
		Side:""
	};
	$scope.status = [];
	$scope.jsonData = 
	{
 "Side": "1",
 "OrderQty": "666",
  "DisplayQty": "1000",
 "Symbol": "F",
 "Price": "500",
 "ExDestination":"XNYS",
 "ClOrdID": "0800200c9a66",
 "TransactTime": "20141003-10:21:37.345",
 "OrdType": "2",
 "Account": "0123456789",
  "OrderCapacity": "I"
};

if (!String.prototype.startsWith) {
  Object.defineProperty(String.prototype, 'startsWith', {
    enumerable: false,
    configurable: false,
    writable: false,
    value: function (searchString, position) {
      position = position || 0;
      return this.lastIndexOf(searchString, position) === position;
    }
  });
}
	$scope.submitform = function(){
		$scope.model.ClOrdID = "" + new Date().getTime();
		$scope.postOrder($scope.model).then(
            function(data) {
				if($scope.model.Symbol.startsWith("F ")){
					data.source = "nyse";
				} else {
					data.source = "lse";
				}
				
				data.Symbol = $scope.model.Symbol;
				
				$scope.status.push(data);
				
				$scope.result = "OK";
			},function(data) { 
				$scope.result = "ERROR";
			}
      ); 
	};
	
	$scope.postOrder = function(jsonData){
		if($scope.model.Symbol.startsWith("F ")){
			return send('POST', 'http://localhost:81/metal/sendorder/nyse', jsonData, "application/json;charset=UTF-8");
		}else{
			return send('POST', 'http://localhost:81/metal/sendorder/lse', jsonData, "application/json;charset=UTF-8");
		}
	};
	
	function send(method, url, jsonData, contentType){ 
        var httpConfig = {"method": method, "url": url, "headers": {"Content-Type": contentType, "Accept" : "*/*"}, "data": jsonData};
      var deferred = $q.defer();
      
      console && console.time && console.time("RESTServices" + url);
		$http(httpConfig).success(function (data) {
            console && console.timeEnd && console.timeEnd("RESTServices" + url);
                    deferred.resolve(data);
                }).error(function (data, status, headers, config) {
                   console.log("REST Error! Failed to execute [" + config.method + "] " + config.url + " Status:" + status);
                   console && console.timeEnd && console.timeEnd("RESTServices" + url);
                   if(status === 403) {
            $window.location.reload();
          }
          else{
                deferred.reject(data);
                    }
                })
            ;
      
            return deferred.promise;
      }

});