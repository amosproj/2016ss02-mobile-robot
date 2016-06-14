'use strict';

describe('myApp.logs module', function() {
  var logsCtrl, scope, service;

  beforeEach(module('myApp.logs'));
  beforeEach(module('myApp.roverService'));
  beforeEach(inject(function($controller, $rootScope, roverService) {
    scope = $rootScope.$new();
    service = roverService;
    logsCtrl = $controller('LogsCtrl', {$scope: scope, RoverService: service});
  }));

  describe('logs controller', function(){

    it('should ....', inject(function($controller) {
      //spec body
      expect(logsCtrl).toBeDefined();
    }));

  });
});