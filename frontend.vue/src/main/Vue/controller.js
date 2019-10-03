import LoginCmp from './components/login/login.vue';
import MainBlock from './components/mainBlock.vue';

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
        'login-cmp': LoginCmp,
        'main-block': MainBlock
    },
    data: {
        user: user
    }
});