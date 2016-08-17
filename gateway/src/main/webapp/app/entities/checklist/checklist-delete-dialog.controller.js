(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ChecklistDeleteController',ChecklistDeleteController);

    ChecklistDeleteController.$inject = ['$uibModalInstance', 'entity', 'Checklist'];

    function ChecklistDeleteController($uibModalInstance, entity, Checklist) {
        var vm = this;

        vm.checklist = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Checklist.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
