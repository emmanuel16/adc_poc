(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('PublishedChecklistDetailController', PublishedChecklistDetailController);

    PublishedChecklistDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'PublishedChecklist', 'Checklist'];

    function PublishedChecklistDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, PublishedChecklist, Checklist) {
        var vm = this;

        vm.publishedChecklist = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('gatewayApp:publishedChecklistUpdate', function(event, result) {
            vm.publishedChecklist = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
