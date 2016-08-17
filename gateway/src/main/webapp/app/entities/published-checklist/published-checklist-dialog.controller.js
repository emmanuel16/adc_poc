(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('PublishedChecklistDialogController', PublishedChecklistDialogController);

    PublishedChecklistDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'PublishedChecklist', 'Checklist'];

    function PublishedChecklistDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, PublishedChecklist, Checklist) {
        var vm = this;

        vm.publishedChecklist = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.checklists = Checklist.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.publishedChecklist.id !== null) {
                PublishedChecklist.update(vm.publishedChecklist, onSaveSuccess, onSaveError);
            } else {
                PublishedChecklist.save(vm.publishedChecklist, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gatewayApp:publishedChecklistUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.publisedDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
