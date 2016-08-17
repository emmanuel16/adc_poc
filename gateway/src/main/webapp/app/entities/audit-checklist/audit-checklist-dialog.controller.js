(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('AuditChecklistDialogController', AuditChecklistDialogController);

    AuditChecklistDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'AuditChecklist', 'ClientInfo'];

    function AuditChecklistDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, AuditChecklist, ClientInfo) {
        var vm = this;

        vm.auditChecklist = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.clientinfos = ClientInfo.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.auditChecklist.id !== null) {
                AuditChecklist.update(vm.auditChecklist, onSaveSuccess, onSaveError);
            } else {
                AuditChecklist.save(vm.auditChecklist, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gatewayApp:auditChecklistUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.modDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
