(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('ClientInfo', ClientInfo);

    ClientInfo.$inject = ['$resource'];

    function ClientInfo ($resource) {
        var resourceUrl =  'audit_checklist/' + 'api/client-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
