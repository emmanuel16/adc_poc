(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ClientInfoDeleteController',ClientInfoDeleteController);

    ClientInfoDeleteController.$inject = ['$uibModalInstance', 'entity', 'ClientInfo'];

    function ClientInfoDeleteController($uibModalInstance, entity, ClientInfo) {
        var vm = this;

        vm.clientInfo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ClientInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
