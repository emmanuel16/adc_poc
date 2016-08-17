(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .factory('ChecklistSearch', ChecklistSearch);

    ChecklistSearch.$inject = ['$resource'];

    function ChecklistSearch($resource) {
        var resourceUrl =  'checklist/' + 'api/_search/checklists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
