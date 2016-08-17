(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('AuditChecklistController', AuditChecklistController);

    AuditChecklistController.$inject = ['$scope', '$state', 'DataUtils', 'AuditChecklist', 'AuditChecklistSearch'];

    function AuditChecklistController ($scope, $state, DataUtils, AuditChecklist, AuditChecklistSearch) {
        var vm = this;
        
        vm.auditChecklists = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            AuditChecklist.query(function(result) {
                vm.auditChecklists = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AuditChecklistSearch.query({query: vm.searchQuery}, function(result) {
                vm.auditChecklists = result;
            });
        }    }
})();
