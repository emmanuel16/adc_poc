(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ClientInfoDialogController', ClientInfoDialogController);

    ClientInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ClientInfo', 'AuditChecklist'];

    function ClientInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ClientInfo, AuditChecklist) {
        var vm = this;

        vm.clientInfo = entity;
        vm.clear = clear;
        vm.save = save;
        vm.auditchecklists = AuditChecklist.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.clientInfo.id !== null) {
                ClientInfo.update(vm.clientInfo, onSaveSuccess, onSaveError);
            } else {
                ClientInfo.save(vm.clientInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gatewayApp:clientInfoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
