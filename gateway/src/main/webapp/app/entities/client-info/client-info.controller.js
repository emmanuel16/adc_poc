(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ClientInfoController', ClientInfoController);

    ClientInfoController.$inject = ['$scope', '$state', 'ClientInfo', 'ClientInfoSearch'];

    function ClientInfoController ($scope, $state, ClientInfo, ClientInfoSearch) {
        var vm = this;
        
        vm.clientInfos = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            ClientInfo.query(function(result) {
                vm.clientInfos = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ClientInfoSearch.query({query: vm.searchQuery}, function(result) {
                vm.clientInfos = result;
            });
        }    }
})();
