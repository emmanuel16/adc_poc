(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('PublishedChecklistController', PublishedChecklistController);

    PublishedChecklistController.$inject = ['$scope', '$state', 'DataUtils', 'PublishedChecklist', 'PublishedChecklistSearch'];

    function PublishedChecklistController ($scope, $state, DataUtils, PublishedChecklist, PublishedChecklistSearch) {
        var vm = this;
        
        vm.publishedChecklists = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            PublishedChecklist.query(function(result) {
                vm.publishedChecklists = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            PublishedChecklistSearch.query({query: vm.searchQuery}, function(result) {
                vm.publishedChecklists = result;
            });
        }    }
})();
