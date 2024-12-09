// 导入Vue及相关库
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

// 导入i18n国际化支持
import i18n from './i18n';

// 导入UI框架及其国际化配置
import ElementPlus from 'element-plus';
import locale from 'element-plus/lib/locale/lang/zh-CN';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

// 导入工具库和配置
import $ from 'jquery';
import VueCookies from 'vue3-cookies';
import * as echarts from 'echarts';
import mitt from 'mitt';

// 导入样式和图标
import 'virtual:svg-icons-register';

// 创建Vue应用实例
const app = createApp(App);

// 开发用
const baseUrl = "http://localhost:8092/talentManageSystem-api/api/v1";
const baseUrl2 = "http://localhost:8092/talentManageSystem-api/api/v2";
//部署用
// const baseUrl = "http://192.168.1.2:8092/talentManageSystem-api/api/v1";
// const baseUrl2 = "http://192.168.1.2:8092/talentManageSystem-api/api/v2";

const emitter = mitt();
localStorage.setItem('lang', 'ja-JP');

// 使用插件和全局属性
app.use(i18n);
app.use(router);
app.use(ElementPlus, { locale });
app.use(VueCookies);

// Element UI图标全局注册
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

// 挂载全局变量
app.config.globalProperties.$echarts = echarts;
app.config.globalProperties.$baseUrl = baseUrl;
app.config.globalProperties.$bus = emitter;

app.config.globalProperties.$http = function (url, method, data, async, fun) {
    $.ajax({
        url: baseUrl + url + (method === 'GET' && data ? '?' + $.param(data) : ''),
        type: method,
        dataType: 'json',
        contentType: "application/json",
        traditional: true,
        xhrFields: {
            withCredentials: true
        },
        headers: {
            "token": localStorage.getItem("token")
        },
        async: async,
        data: method !== 'GET' ? JSON.stringify(data) : null,
        success: function (resp) {
            if (resp.code == 200) {
                fun(resp)
            } else {
                ElMessage.error({
                    message: resp.msg,
                    type: 'warning',
                    duration: 1200
                });
            }
        },
        error: function (e) {
            if (e.status == undefined) {
                ElMessage.error({
                    message: "前端页面错误",
                    duration: 1200
                });
            } else {
                let status = e.status
                if (status == 401) {
                    router.push({ name: 'Login' });
                } else {
                    ElMessage({
                        message: e.responseJSON.msg,
                        type: 'warning',
                        duration: 1200
                    });
                }
            }
        }
    });
};

app.config.globalProperties.$httpV2 = function (url, method, data, async, fun) {
    $.ajax({
        url: baseUrl2 + url + (method === 'GET' && data ? '?' + $.param(data) : ''),
        type: method,
        dataType: 'json',
        contentType: "application/json",
        traditional: true,
        xhrFields: {
            withCredentials: true
        },
        headers: {
            "token": localStorage.getItem("token")
        },
        async: async,
        data: method !== 'GET' ? JSON.stringify(data) : null,
        success: function (resp) {
            if (resp.code == 200) {
                fun(resp)
            } else {
                ElMessage.error({
                    message: resp.msg,
                    type: 'warning',
                    duration: 1200
                });
            }
        },
        error: function (e) {
            if (e.status == undefined) {
                ElMessage.error({
                    message: "前端页面错误",
                    duration: 1200
                });
            } else {
                let status = e.status
                if (status == 401) {
                    router.push({ name: 'Login' });
                } else {
                    ElMessage({
                        message: e.responseJSON.msg,
                        type: 'warning',
                        duration: 1200
                    });
                }
            }
        }
    });
};
app.config.globalProperties.isAuth = function (permission) {
    let permissions = localStorage.getItem("permissions");
    let flag = false;
    for (let one of permission) {
        if (permissions.includes(one)) {
            flag = true;
            break;
        }
    }
    return flag;
};

app.config.globalProperties.$searchDict = function (type) {
    let dictDatas = [];
    this.$http('/dict/' + type, 'GET', null, false, function (resp) {
        dictDatas = resp.result;
    });
    return dictDatas;
};

// 挂载应用实例
app.mount('#app');