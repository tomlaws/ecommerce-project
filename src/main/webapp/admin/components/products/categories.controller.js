(function () {
    'use strict';

    angular
        .module('app')
        .controller('CategoriesCtrl', CategoriesCtrl);

    CategoriesCtrl.$inject = ['categoryService'];

    function CategoriesCtrl(categoryService) {
        var vm = this;

        categoryService.getCategories()
            .then(function successCallback(response) {
                console.log(response);
                vm.categories = response.data;
            }, function errorCallback(response) {
                console.log(response);
            });

        vm.saveChanges = function () {
            var json = angular.toJson(vm.categories); // Remove $$hashkey generated by AngularJS
            console.log(json);
            categoryService.updateCategories(json)
                .then(function successCallback(response) {
                    delete vm.failure;
                    vm.success = true;
                    vm.categories = response.data;
                    console.log(response);
                }, function errorCallback(response) {
                    delete vm.success;
                    vm.failure = true;
                    console.log(response);
                });
        };

        vm.addCategory = function () {
            if (vm.categoryName) {
                var newCategory = {
                    name: vm.categoryName,
                    order: 0,
                    children: []
                };
                vm.categories.push(newCategory);
            }
        };

        vm.removeCategory = function (element) {
            element.remove();
        };

        vm.editCategoryName = function (id) {
            if (!vm.edit)
                vm.edit = {};
            vm.edit[id] = !vm.edit[id];
        };
    }
})();