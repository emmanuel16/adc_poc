(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('PublishedChecklist', PublishedChecklist);

    PublishedChecklist.$inject = ['$resource', 'DateUtils'];

    function PublishedChecklist ($resource, DateUtils) {
        var resourceUrl =  'checklist/' + 'api/published-checklists/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.publisedDate = DateUtils.convertLocalDateFromServer(data.publisedDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.publisedDate = DateUtils.convertLocalDateToServer(data.publisedDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.publisedDate = DateUtils.convertLocalDateToServer(data.publisedDate);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
