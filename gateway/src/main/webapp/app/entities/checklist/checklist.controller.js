(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ChecklistController', ChecklistController);

    ChecklistController.$inject = ['$scope', '$state', 'DataUtils', 'Checklist', 'ChecklistSearch'];

    function ChecklistController ($scope, $state, DataUtils, Checklist, ChecklistSearch) {
        var vm = this;
        
        vm.checklists = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Checklist.query(function(result) {
                vm.checklists = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ChecklistSearch.query({query: vm.searchQuery}, function(result) {
                vm.checklists = result;
            });
        }    }
})();
