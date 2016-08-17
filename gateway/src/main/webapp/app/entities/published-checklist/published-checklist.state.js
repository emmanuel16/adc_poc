(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('published-checklist', {
            parent: 'entity',
            url: '/published-checklist',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gatewayApp.publishedChecklist.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/published-checklist/published-checklists.html',
                    controller: 'PublishedChecklistController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('publishedChecklist');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('published-checklist-detail', {
            parent: 'entity',
            url: '/published-checklist/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gatewayApp.publishedChecklist.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/published-checklist/published-checklist-detail.html',
                    controller: 'PublishedChecklistDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('publishedChecklist');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'PublishedChecklist', function($stateParams, PublishedChecklist) {
                    return PublishedChecklist.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'published-checklist',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('published-checklist-detail.edit', {
            parent: 'published-checklist-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/published-checklist/published-checklist-dialog.html',
                    controller: 'PublishedChecklistDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PublishedChecklist', function(PublishedChecklist) {
                            return PublishedChecklist.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('published-checklist.new', {
            parent: 'published-checklist',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/published-checklist/published-checklist-dialog.html',
                    controller: 'PublishedChecklistDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                year: null,
                                data: null,
                                grp: null,
                                domain: null,
                                type: null,
                                description: null,
                                current: null,
                                active: null,
                                publisedDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('published-checklist', null, { reload: true });
                }, function() {
                    $state.go('published-checklist');
                });
            }]
        })
        .state('published-checklist.edit', {
            parent: 'published-checklist',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/published-checklist/published-checklist-dialog.html',
                    controller: 'PublishedChecklistDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PublishedChecklist', function(PublishedChecklist) {
                            return PublishedChecklist.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('published-checklist', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('published-checklist.delete', {
            parent: 'published-checklist',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/published-checklist/published-checklist-delete-dialog.html',
                    controller: 'PublishedChecklistDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PublishedChecklist', function(PublishedChecklist) {
                            return PublishedChecklist.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('published-checklist', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
