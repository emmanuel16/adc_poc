(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .factory('PublishedChecklistSearch', PublishedChecklistSearch);

    PublishedChecklistSearch.$inject = ['$resource'];

    function PublishedChecklistSearch($resource) {
        var resourceUrl =  'checklist/' + 'api/_search/published-checklists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
