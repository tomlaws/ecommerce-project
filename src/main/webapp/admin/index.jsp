<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html ng-app="app">

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title ng-bind="title + ' | Admin Panel'">Admin Panel</title>
    <base href="/admin/" />
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
        integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <link rel="stylesheet" href="assets/css/style.css">
    <link rel="stylesheet" href="assets/css/angular-ui-tree.min.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.2/css/all.css"
        integrity="sha384-fnmOCqbTlWIlj8LyTjo7mOUStjsKC4pOpQbqyi7RrhN7udi9RwhKkMHpvLbHG9Sr" crossorigin="anonymous">

    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.7.7/angular.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.7.7/angular-route.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.7.7/angular-animate.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.7.7/angular-aria.min.js"></script>
    <script src="assets/js/angular-ui-tree.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
        integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous">
    </script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
        integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous">
    </script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
        integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous">
    </script>
    <script src="https://unpkg.com/angular-datetime-input"></script>

    <script src="app.module.js"></script>
    <script src="components/sidebar/sidebar.controller.js"></script>
    <script src="components/sidebar/sidebar.directive.js"></script>
    <script src="components/dashboard/dashboard.controller.js"></script>
    <script src="components/dashboard/dashboard.directive.js"></script>
    <script src="components/dashboard/dashboard.service.js"></script>
    <script src="components/users/all-users.controller.js"></script>
    <script src="components/users/all-users.directive.js"></script>
    <script src="components/users/edit-user.controller.js"></script>
    <script src="components/users/edit-user.directive.js"></script>
    <script src="components/users/add-user.controller.js"></script>
    <script src="components/users/add-user.directive.js"></script>

    <script src="components/products/categories.controller.js"></script>
    <script src="components/products/categories.directive.js"></script>
    <script src="components/products/products.controller.js"></script>
    <script src="components/products/products.directive.js"></script>
    <script src="components/products/edit-product.controller.js"></script>
    <script src="components/products/edit-product.directive.js"></script>
    <script src="components/products/add-product.controller.js"></script>
    <script src="components/products/add-product.directive.js"></script>

    <script src="components/orders/customer-orders.controller.js"></script>
    <script src="components/orders/customer-orders.directive.js"></script>
    <script src="components/orders/customer-order.controller.js"></script>
    <script src="components/orders/customer-order.directive.js"></script>

    <script src="components/settings/site-config.controller.js"></script>
    <script src="components/settings/site-config.directive.js"></script>

    <script src="components/users/user.service.js"></script>
    <script src="components/products/category.service.js"></script>
    <script src="components/products/product.service.js"></script>
    <script src="components/orders/order.service.js"></script>
    <script src="components/settings/setting.service.js"></script>

    <script src="shared/pagination/pagination.controller.js"></script>
    <script src="shared/pagination/pagination.directive.js"></script>
    <script src="shared/upload/upload.directive.js"></script>

    <script src="shared/search/search.service.js"></script>
</head>

<body ng-cloak>
    <div class="container-fluid p-0">
        <sidebar></sidebar>
        <div class="col-12 col-md-9 col-lg-10 col-xl-10 ng-scope float-right">
            <div ng-view class="admin-view"></div>
        </div>
    </div>
</body>

</html>