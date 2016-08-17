(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ChecklistDetailController', ChecklistDetailController);

    ChecklistDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Checklist', 'PublishedChecklist'];

    function ChecklistDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Checklist, PublishedChecklist) {
        var vm = this;

        vm.checklist = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('gatewayApp:checklistUpdate', function(event, result) {
            vm.checklist = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
