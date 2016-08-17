(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('client-info', {
            parent: 'entity',
            url: '/client-info',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gatewayApp.clientInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/client-info/client-infos.html',
                    controller: 'ClientInfoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('clientInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('client-info-detail', {
            parent: 'entity',
            url: '/client-info/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'gatewayApp.clientInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/client-info/client-info-detail.html',
                    controller: 'ClientInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('clientInfo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ClientInfo', function($stateParams, ClientInfo) {
                    return ClientInfo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'client-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('client-info-detail.edit', {
            parent: 'client-info-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/client-info/client-info-dialog.html',
                    controller: 'ClientInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ClientInfo', function(ClientInfo) {
                            return ClientInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('client-info.new', {
            parent: 'client-info',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/client-info/client-info-dialog.html',
                    controller: 'ClientInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('client-info', null, { reload: true });
                }, function() {
                    $state.go('client-info');
                });
            }]
        })
        .state('client-info.edit', {
            parent: 'client-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/client-info/client-info-dialog.html',
                    controller: 'ClientInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ClientInfo', function(ClientInfo) {
                            return ClientInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('client-info', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('client-info.delete', {
            parent: 'client-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/client-info/client-info-delete-dialog.html',
                    controller: 'ClientInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ClientInfo', function(ClientInfo) {
                            return ClientInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('client-info', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
