(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('AuditChecklist', AuditChecklist);

    AuditChecklist.$inject = ['$resource', 'DateUtils'];

    function AuditChecklist ($resource, DateUtils) {
        var resourceUrl =  'audit_checklist/' + 'api/audit-checklists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.modDate = DateUtils.convertLocalDateFromServer(data.modDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.modDate = DateUtils.convertLocalDateToServer(data.modDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.modDate = DateUtils.convertLocalDateToServer(data.modDate);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
