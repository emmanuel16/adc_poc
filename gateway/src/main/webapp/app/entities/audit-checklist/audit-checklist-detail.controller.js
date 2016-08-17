(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('AuditChecklistDetailController', AuditChecklistDetailController);

    AuditChecklistDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'AuditChecklist', 'ClientInfo'];

    function AuditChecklistDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, AuditChecklist, ClientInfo) {
        var vm = this;

        vm.auditChecklist = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('gatewayApp:auditChecklistUpdate', function(event, result) {
            vm.auditChecklist = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
