(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .factory('ClientInfoSearch', ClientInfoSearch);

    ClientInfoSearch.$inject = ['$resource'];

    function ClientInfoSearch($resource) {
        var resourceUrl =  'audit_checklist/' + 'api/_search/client-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
