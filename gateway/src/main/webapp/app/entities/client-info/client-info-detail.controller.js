(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ClientInfoDetailController', ClientInfoDetailController);

    ClientInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ClientInfo', 'AuditChecklist'];

    function ClientInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, ClientInfo, AuditChecklist) {
        var vm = this;

        vm.clientInfo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gatewayApp:clientInfoUpdate', function(event, result) {
            vm.clientInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
