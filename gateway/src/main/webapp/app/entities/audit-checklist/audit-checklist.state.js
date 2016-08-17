(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('audit-checklist', {
            parent: 'entity',
            url: '/audit-checklist',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gatewayApp.auditChecklist.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/audit-checklist/audit-checklists.html',
                    controller: 'AuditChecklistController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('auditChecklist');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('audit-checklist-detail', {
            parent: 'entity',
            url: '/audit-checklist/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gatewayApp.auditChecklist.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/audit-checklist/audit-checklist-detail.html',
                    controller: 'AuditChecklistDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('auditChecklist');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'AuditChecklist', function($stateParams, AuditChecklist) {
                    return AuditChecklist.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'audit-checklist',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('audit-checklist-detail.edit', {
            parent: 'audit-checklist-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audit-checklist/audit-checklist-dialog.html',
                    controller: 'AuditChecklistDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AuditChecklist', function(AuditChecklist) {
                            return AuditChecklist.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('audit-checklist.new', {
            parent: 'audit-checklist',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audit-checklist/audit-checklist-dialog.html',
                    controller: 'AuditChecklistDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                publishedChecklistId: null,
                                year: null,
                                data: null,
                                grp: null,
                                domain: null,
                                type: null,
                                description: null,
                                modDate: null,
                                lastModBy: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('audit-checklist', null, { reload: true });
                }, function() {
                    $state.go('audit-checklist');
                });
            }]
        })
        .state('audit-checklist.edit', {
            parent: 'audit-checklist',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audit-checklist/audit-checklist-dialog.html',
                    controller: 'AuditChecklistDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AuditChecklist', function(AuditChecklist) {
                            return AuditChecklist.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('audit-checklist', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('audit-checklist.delete', {
            parent: 'audit-checklist',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audit-checklist/audit-checklist-delete-dialog.html',
                    controller: 'AuditChecklistDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AuditChecklist', function(AuditChecklist) {
                            return AuditChecklist.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('audit-checklist', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
