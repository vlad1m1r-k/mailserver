<template>
    <div class="l-modal" v-if="userRole !== 'USER' || userRole !== 'ADMIN'">
        <div class="l-modal-body">
            <div class="row">
                <div class="col">
                    <div class="btn-group btn-block">
                        <button class="btn btn-outline-info" @click="currentTab = 'LoginTab'"
                        :class="{'active-tab': currentTab === 'LoginTab'}">Login</button>
                        <button class="btn btn-outline-info" @click="currentTab = 'RegisterTab'"
                        :class="{'active-tab': currentTab === 'RegisterTab'}">Register</button>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-auto">
                    <component :is="currentTab"></component>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import LoginTab from './loginTab.vue';
    import RegisterTab from './registerTab.vue';

    export default {
        name: "login",
        components: {
            LoginTab,
            RegisterTab
        },
        props: {
            'user-role': String
        },
        data() {
            return {
                currentTab: 'LoginTab'
            }
        }
    }
</script>

<style scoped>
    .l-modal {
        left: 0;
        top:0;
        right: 0;
        bottom: 0;
        position: fixed;
        background: rgba(0, 0, 0, 0.69);
        z-index: 1050;
        transition: all 0.4s ease-in-out;
        -moz-transition: all 0.4s ease-in-out;
        -webkit-transition: all 0.4s ease-in-out;
        margin: 0;
        padding: 0;
        pointer-events: auto;
    }
    .l-modal-body {
        padding: 15px;
        width: 400px;
        margin: 30px auto;
        border-radius: 5px;
        background: #fff;
        box-shadow: 0 3px 7px rgba(0,0,0,.25);
        -moz-box-shadow: 0 3px 7px rgba(0,0,0,.25);
        -webkit-box-shadow: 0 3px 7px rgba(0,0,0,.25);
        box-sizing: border-box;
        -moz-box-sizing: border-box;
        -webkit-box-sizing: border-box;
    }
    .active-tab {
        background-color: #bde6ec;
    }
</style>