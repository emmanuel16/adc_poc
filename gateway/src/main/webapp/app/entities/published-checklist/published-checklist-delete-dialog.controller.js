(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('PublishedChecklistDeleteController',PublishedChecklistDeleteController);

    PublishedChecklistDeleteController.$inject = ['$uibModalInstance', 'entity', 'PublishedChecklist'];

    function PublishedChecklistDeleteController($uibModalInstance, entity, PublishedChecklist) {
        var vm = this;

        vm.publishedChecklist = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            PublishedChecklist.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
