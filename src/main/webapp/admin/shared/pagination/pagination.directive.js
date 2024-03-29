angular
    .module('app')
    .directive('pagination', pagination);

function pagination() {
    var directive = {
        replace: true,
        templateUrl: '/shared/pagination/pagination.html',
        restrict: 'EA',
        scope: {
            currPage: '=',
            maxPages: '=',
            hrefPrefix: '@',
            func: '&?'
        },
        controller: 'PaginationCtrl',
        controllerAs: 'paginationCtrl',
        bindToController: true
    };
    return directive;
}