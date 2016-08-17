(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .factory('AuditChecklistSearch', AuditChecklistSearch);

    AuditChecklistSearch.$inject = ['$resource'];

    function AuditChecklistSearch($resource) {
        var resourceUrl =  'audit_checklist/' + 'api/_search/audit-checklists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
