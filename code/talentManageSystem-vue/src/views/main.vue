<template>
  <!-- 根div -->
  <div
    class="site-wrapper"
    :class="{ 'site-sidebar--fold': sidebarFold }"
    v-loading.fullscreen.lock="loading"
    element-loading-text="拼命加载中"
  >
    <!-- 侧边栏显示隐藏 -->
    <nav
      class="site-navbar"
      :class="'site-navbar--' + navbarLayoutType"
    >
      <div class="site-navbar__header">
        <h1 class="site-navbar__brand">
          <div v-if="isAuth(['talent_search_basic_info'])">
            <a class="site-navbar__brand-lg">
              {{ $t("Talent Management System") }}</a>
            <a class="site-navbar__brand-mini">{{ $t("Talent") }}</a>
          </div>
          <div v-if="isAuth(['excel_edit'])">
            <a class="site-navbar__brand-lg">フォーム管理</a>
            <a class="site-navbar__brand-mini">管理</a>
          </div>
        </h1>
      </div>
      <div
        class="navbar-container"
        :class="'navbar-container--' + navbarLayoutType"
      >
        <div
          class="switch"
          @click="handleSwitch"
        >
          <SvgIcon
            name="zhedie"
            class="icon-svg"
          />
        </div>
        <!-- 上边栏 -->
        <div class="right-container">
          <el-radio-group
            v-if="isAuth(['talent_search_basic_info'])"
            label="{{ $t('Language') }}"
            v-model="lang"
            style="margin-right: 30px"
          >
            <el-radio-button label="zh-CN">{{ $t("Chinese") }}</el-radio-button>
            <el-radio-button label="ja-JP">{{
              $t("Japanese-lang")
            }}</el-radio-button>
          </el-radio-group>
          <el-dropdown>
            <span v-if="isAuth(['excel_edit'])">こんにちは！</span>
            <span
              v-else
              class="el-dropdown-link"
            >{{ $t("Hello!") }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">
                  <span v-if="isAuth(['excel_edit'])">サインアウト</span>
                  <span v-else>{{ $t("Logout") }}</span>

                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <!-- 人才管理侧边栏 -->
    <aside
      class="site-sidebar site-sidebar--dark"
      v-if="isAuth(['talent_search_basic_info'])"
    >
      <div class="site-sidebar__inner">
        <el-menu
          :default-active="menuActiveName || 'home'"
          :collapse="sidebarFold"
          :collapseTransition="false"
          class="site-sidebar__menu"
          background-color="#263238"
          active-text-color="#fff"
          text-color="#8a979e"
        >
          <el-menu-item
            index="{{ $t('Home') }}"
            @click="$router.push({ name: 'Home' })"
            v-if="isAuth(['talent_search_basic_info'])"
          >
            <SvgIcon
              name="home"
              class="icon-svg"
            />
            <span slot="title">{{ $t("Home") }}</span>
          </el-menu-item>
          <!-- 人才管理 -->
          <el-sub-menu
            index="{{ $t('Talent Management') }}"
            :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
            v-if="
              isAuth([
                'talent_search_lev1secret_info',
                'talent_search_lev2secret_info',
                'talent_search_basic_info',
                'talent_add',
                'talent_update',
                'talent_delete',
                'talent_export',
                'talent_skill_sheet',
                'talent_add_blacklist',
                'talent_remove_blacklist',
                'talent_search_blacklist',
                'talent_update_blacklist',
                'talent_search_deletelist',
                'talent_recover_deletelist',
              ])
            "
          >
            <template #title>
              <SvgIcon
                name="users_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Talent Management") }}</span>
            </template>
            <el-menu-item
              index="{{ $t('Talent Search') }}"
              v-if="
                isAuth([
                  'talent_search_lev1secret_info',
                  'talent_search_lev2secret_info',
                  'talent_search_basic_info',
                  'talent_add',
                  'talent_update',
                  'talent_delete',
                  'talent_export',
                  'talent_skill_sheet',
                ])
              "
              @click="$router.push({ name: 'Talent' })"
            >
              <SvgIcon
                name="company_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Talent Search") }}</span>
            </el-menu-item>
            <el-menu-item
              index="{{ $t('Blacklist') }}"
              v-if="
                isAuth([
                  'talent_add_blacklist',
                  'talent_remove_blacklist',
                  'talent_search_blacklist',
                  'talent_update_blacklist',
                ])
              "
              @click="$router.push({ name: 'Blacklist' })"
            >
              <SvgIcon
                name="company_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Blacklist") }}</span>
            </el-menu-item>
            <el-menu-item
              index="{{ $t('Delete Library') }}"
              v-if="
                isAuth([
                  'talent_search_deletelist',
                  'talent_recover_deletelist',
                ])
              "
              @click="$router.push({ name: 'Deletelist' })"
            >
              <SvgIcon
                name="company_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Delete Library") }}</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 标签管理 -->
          <el-sub-menu
            index="{{ $t('Tag Management') }}"
            :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
            v-if="
              isAuth([
                'label_search',
                'label_add',
                'label_update',
                'label_delete',
              ])
            "
          >
            <template #title>
              <SvgIcon
                name="night_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Tag Management") }}</span>
            </template>
            <el-menu-item
              index="{{ $t('Tag Management') }}"
              v-if="
                isAuth([
                  'label_search',
                  'label_add',
                  'label_update',
                  'label_delete',
                ])
              "
              @click="$router.push({ name: 'Label' })"
            >
              <SvgIcon
                name="calendar_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Tag Management") }}</span>
            </el-menu-item>
            <el-menu-item
              index="{{ $t('Tag Category Management') }}"
              v-if="
                isAuth([
                  'label_search',
                  'label_add',
                  'label_update',
                  'label_delete',
                ])
              "
              @click="$router.push({ name: 'Labeltype' })"
            >
              <SvgIcon
                name="clock_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Tag Category Management") }}</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 行业管理 -->
          <el-sub-menu
            index="{{ $t('Industry Management') }}"
            :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
            @click="$router.push({ name: 'Business' })"
          >
            <template #title>
              <SvgIcon
                name="system_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Industry Management") }}</span>
            </template>
          </el-sub-menu>

          <!-- 面试统计管理 -->
          <el-sub-menu
            index="{{ $t('Interview Statistics Management') }}"
            :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
            v-if="
              isAuth([
                'interviewer_add',
                'interviewer_del',
                'interviewer_update',
                'interviewer_search',
                'inter_statistics',
              ])
            "
          >
            <template #title>
              <SvgIcon
                name="night_fill"
                class="icon-svg"
              />
              <span slot="title">{{
                $t("Interview Statistics Management")
              }}</span>
            </template>
            <el-menu-item
              index="{{ $t('Interviewer Management') }}"
              v-if="
                isAuth([
                  'interviewer_add',
                  'interviewer_del',
                  'interviewer_update',
                  'interviewer_search',
                ])
              "
              @click="$router.push({ name: 'Interviewer' })"
            >
              <SvgIcon
                name="calendar_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Interviewer Management") }}</span>
            </el-menu-item>
            <el-menu-item
              index="{{ $t('Data Statistics') }}"
              v-if="isAuth(['inter_statistics'])"
              @click="$router.push({ name: 'Statistics' })"
            >
              <SvgIcon
                name="clock_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Data Statistics") }}</span>
            </el-menu-item>
          </el-sub-menu>
          <!-- 用户管理 -->
          <el-sub-menu
            index="{{ $t('User Management') }}"
            :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
            v-if="
              isAuth([
                'supadm_search',
                'supadm_create',
                'supadm_delete',
                'supadm_update',
                'supadm_disable',
                'ordadm_search',
                'ordadm_create',
                'ordadm_delete',
                'ordadm_update',
                'ordadm_disable',
                'user_search',
                'user_create',
                'user_delete',
                'user_update',
                'user_disable',
              ])
            "
            @click="$router.push({ name: 'User' })"
          >
            <template #title>
              <SvgIcon
                name="system_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("User Management") }}</span>
            </template>
          </el-sub-menu>
          <!-- 日志管理 -->
          <el-sub-menu
            index="{{ $t('Log Management') }}"
            :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
            v-if="isAuth(['log'])"
            @click="$router.push({ name: 'Log' })"
          >
            <template #title>
              <SvgIcon
                name="system_fill"
                class="icon-svg"
              />
              <span slot="title">{{ $t("Log Management") }}</span>
            </template>
          </el-sub-menu>
        </el-menu>
      </div>
    </aside>

    <!-- 表格管理侧边栏 -->
    <aside
      class="site-sidebar site-sidebar--dark"
      v-if="isAuth(['excel_edit'])"
    >
      <div class="site-sidebar__inner">
        <el-menu
          :default-active="menuActiveName || 'menu'"
          :collapse="sidebarFold"
          :collapseTransition="false"
          class="site-sidebar__menu"
          background-color="#263238"
          active-text-color="#fff"
          text-color="#8a979e"
        >
          <el-menu-item
            index="メニュー"
            @click="$router.push({ name: 'Menu' })"
          >
            <SvgIcon
              name="home"
              class="icon-svg"
            />
            <span slot="title">メニュー</span>
          </el-menu-item>
          <el-menu-item
            @click="$router.push({ name: 'Appoint' })"
            v-if="isAuth(['price_search'])"
          >
            <SvgIcon
              name="company_fill"
              class="icon-svg"
            />
            <span slot="title">予約管理</span>
          </el-menu-item>
          <el-menu-item
            index="技術者管理"
            v-if="isAuth(['price_search'])"
            @click="$router.push({ name: 'Technician' })"
          >
            <SvgIcon
              name="calendar_fill"
              class="icon-svg"
            />
            <span slot="title">技術者管理</span>
          </el-menu-item>
          <el-menu-item
            index="プロジェクト管理"
            @click="$router.push({ name: 'Project' })"
            v-if="isAuth(['price_search'])"
          >
            <SvgIcon
              name="clock_fill"
              class="icon-svg"
            />
            <span slot="title">プロジェクト管理</span>
          </el-menu-item>

          <el-menu-item
            index="company"
            v-if="isAuth(['price_search'])"
            @click="$router.push({ name: 'Company' })"
          >
            <SvgIcon
              name="company_fill"
              class="icon-svg"
            />
            <span slot="title">協力会社情報</span>
          </el-menu-item>
        </el-menu>
      </div>
    </aside>

    <div
      class="site-content__wrapper"
      :style="{ 'min-height': documentClientHeight + 'px' }"
    >
      <main
        class="site-content"
        :class="{ 'site-content--tabs': $route.meta.isTab }"
      >
        <el-tabs
          v-if="$route.meta.isTab"
          v-model="mainTabsActiveName"
          :closable="true"
          @tab-click="selectedTabHandle"
          @tab-remove="removeTabHandle"
        >
          <el-tab-pane
            v-for="item in mainTabs"
            :label="item.title"
            :name="item.name"
          >
            <el-card :body-style="siteContentViewHeight">
              <iframe
                v-if="item.type === 'iframe'"
                :src="item.iframeUrl"
                width="100%"
                height="100%"
                frameborder="0"
                scrolling="yes"
              ></iframe>
              <router-view v-if="item.name === mainTabsActiveName" />
            </el-card>
          </el-tab-pane>
        </el-tabs>
        <el-card
          v-else
          :body-style="siteContentViewHeight"
        ><router-view /></el-card>
      </main>
    </div>
  </div>
</template>


<script>
import SvgIcon from "../components/SvgIcon.vue";
import { isURL } from "../utils/validate";
import { ref, provide } from "vue";
export default {
  components: { SvgIcon },
  data: function () {
    return {
      navbarLayoutType: "",
      sidebarFold: false,
      sidebarLayoutSkin: "dark",
      name: "",
      photo: "",
      documentClientHeight: 0,
      siteContentViewHeight: {},
      height: null,
      mainTabs: [],
      mainTabsActiveName: "dept",
      menuActiveName: "",
      updatePasswordVisible: false,
      lang: localStorage.getItem("lang"),
    };
  },
  computed: {
    homePage() {
      if (this.isAuth(["talent_search_basic_info"])) {
        return "Home";
      } else if (this.isAuth(["excel_edit"])) {
        return "Menu";
      } else {
        return "";
      }
    },
  },
  created() {
    let that = this;
    that.routeHandle(that.$route);
  },
  watch: {
    $route: {
      handler(to, from) {
        if (to.path != from.path) {
          // this.$router.push(to);
          this.routeHandle(to);
        }
      },
    },
    lang(newVal, oldVal) {
      let that = this;
      // 修改loaclstroge
      localStorage.setItem("lang", newVal);
      this.$i18n.locale = localStorage.getItem("lang");
      let url = "";
      if (newVal === "zh-CN") {
        url = "/redis/zh-CN";
      } else if (newVal === "ja-JP") {
        url = "/redis/ja-JP";
      }
      // 修改后端redis的lang
      that.$http(url, "PUT", null, false, function (resp) {});
      // 刷新当前所有的tab
      that.$bus.emit("refreshTabs");
    },
  },
  methods: {
    handleSwitch: function () {
      if (this.sidebarFold) {
        this.navbarLayoutType = "";
      } else {
        this.navbarLayoutType = "fold";
      }
      this.sidebarFold = !this.sidebarFold;
    },
    resetDocumentClientHeight: function () {
      this.documentClientHeight = document.documentElement["clientHeight"];
      window.onresize = () => {
        this.documentClientHeight = document.documentElement["clientHeight"];
        this.loadSiteContentViewHeight();
      };
    },
    loadSiteContentViewHeight: function () {
      let height = this.documentClientHeight - 50 - 30 - 2;
      if (this.$route.meta.isTab) {
        height -= 40;
        this.siteContentViewHeight = isURL(this.$route.meta.iframeUrl)
          ? { height: height + "px" }
          : { minHeight: height + "px" };
        this.height = provide("height", { height: height - 40 + "px" });
      }
      this.siteContentViewHeight = { minHeight: height + "px" };
    },
    routeHandle: function (route) {
      //每次切换页面，重新计算页面高度和内容区高度
      this.resetDocumentClientHeight();
      this.loadSiteContentViewHeight();

      if (route.meta.isTab) {
        // tab选中, 不存在先添加
        var tab = this.mainTabs.filter((item) => item.name === route.name)[0];
        if (!tab) {
          if (route.meta.isDynamic) {
            route = this.dynamicMenuRoutes.filter(
              (item) => item.name === route.name
            )[0];

            if (!route) {
              return console.error("未能找到可用标签页!");
            }
          }
          tab = {
            menuId: route.meta.menuId || route.name,
            name: route.name,
            title: route.meta.title,
            type: isURL(route.meta.iframeUrl) ? "iframe" : "module",
            iframeUrl: route.meta.iframeUrl || "",
            params: route.params,
            query: route.query,
          };
          this.mainTabs = this.mainTabs.concat(tab);
        }
        this.menuActiveName = tab.menuId + "";
        this.mainTabsActiveName = tab.name;
      }
    },
    // 退出登录
    logout: function () {
      let that = this;
      that.$http("/login/logout", "GET", null, true, function (resp) {
        localStorage.removeItem("permissions");
        localStorage.removeItem("token");
        that.$router.push({ name: "Login" });
      });
    },
    updatePasswordHandle: function () {
      this.updatePasswordVisible = true;
      this.$nextTick(() => {
        this.$refs.updatePassword.init();
      });
    },
    selectedTabHandle: function (tab, e) {
      tab = this.mainTabs.filter((item) => item.name === tab.paneName);
      if (tab.length >= 1) {
        this.$router.push({
          name: tab[0].name,
          query: tab[0].query,
          params: tab[0].params,
        });
      }
    },
    removeTabHandle: function (tabName) {
      this.mainTabs = this.mainTabs.filter((item) => item.name !== tabName);
      if (this.mainTabs.length >= 1) {
        // 当前选中tab被删除
        if (tabName === this.mainTabsActiveName) {
          var tab = this.mainTabs[this.mainTabs.length - 1];
          this.$router.push(
            { name: tab.name, query: tab.query, params: tab.params },
            () => {
              this.mainTabsActiveName = this.$route.name;
            }
          );
        }
      } else {
        this.menuActiveName = "";
        this.$router.push({ name: this.homePage });
      }
    },
    // tabs, 关闭当前
    tabsCloseCurrentHandle: function () {
      this.removeTabHandle(this.mainTabsActiveName);
    },
    // tabs, 关闭其它
    tabsCloseOtherHandle: function () {
      this.mainTabs = this.mainTabs.filter(
        (item) => item.name === this.mainTabsActiveName
      );
    },
    // tabs, 关闭全部
    tabsCloseAllHandle: function () {
      this.mainTabs = [];
      this.menuActiveName = "";
      this.$router.push({ name: this.homePage });
    },
  },
  mounted: function () {
    let that = this;
    that.resetDocumentClientHeight();
    that.loadSiteContentViewHeight();
  },
};
</script>

<style lang="scss">
@import "../assets/scss/index.scss";
</style>
