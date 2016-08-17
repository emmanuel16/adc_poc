(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('AuditChecklistDeleteController',AuditChecklistDeleteController);

    AuditChecklistDeleteController.$inject = ['$uibModalInstance', 'entity', 'AuditChecklist'];

    function AuditChecklistDeleteController($uibModalInstance, entity, AuditChecklist) {
        var vm = this;

        vm.auditChecklist = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            AuditChecklist.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
