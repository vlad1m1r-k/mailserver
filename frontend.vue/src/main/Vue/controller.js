import LoginCmp from './components/login.vue';

Vue.use({
    install: function (Vue) {
        Vue.errorHandler = function (error) {
            if (error.status === 401) {
                vm.user.role = 'ROLE_ANONYMOUS';
                // location.href = '/';
            }
            if (error.responseJSON) {
                return 'Error ' + error.responseJSON.status + ' ' + error.responseJSON.error + ' ' + error.responseJSON.message;
            }
            return 'Error ' + error.status;
        }
    }
});

const vm = new Vue({
    el: '#app',
    components: {
        'login-cmp': LoginCmp
    },
    data: {
        user: user
    }
});