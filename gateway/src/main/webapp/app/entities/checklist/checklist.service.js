(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('Checklist', Checklist);

    Checklist.$inject = ['$resource'];

    function Checklist ($resource) {
        var resourceUrl =  'checklist/' + 'api/checklists/:id';

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
