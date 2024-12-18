import {
	createRouter,
	createWebHashHistory
}
	from 'vue-router'

import NotFound from "../views/404.vue"
import Login from "../views/login.vue"
import Main from "../views/main.vue"
import Home from "../views/home.vue"
import Talent from "../views/talent.vue"
import Blacklist from "../views/blacklist.vue"
import Deletelist from "../views/deletelist.vue"
import Label from "../views/label.vue"
import Labeltype from "../views/label_type.vue"
import Business from "../views/business.vue"
import Interviewer from "../views/interviewer.vue"
import Statistics from "../views/statistics.vue"
import User from "../views/user.vue"
import Log from "../views/log.vue"
import Settings from "../views/settings.vue"
// 表格管理
import Menu from "../views/excelSystem/menu.vue"
import Appoint from "../views/excelSystem/appoint.vue"
import Technician from "../views/excelSystem/technician.vue"
import Project from "../views/excelSystem/project.vue"
import Customer from "../views/excelSystem/customer.vue"
import UserTable from "../views/excelSystem/user_table.vue"
import ProjectTable from "../views/excelSystem/project_table.vue"
import WorkTable from "../views/excelSystem/work_table.vue"
import Company from "../views/excelSystem/company.vue"

const routes = [
	{
		path: '/login',
		name: 'Login',
		component: Login
	},
	{
		path: '/',
		name: 'Main',
		component: Main,
		children: [
			{
				path: '/menu',
				name: 'Menu',
				component: Menu,
				meta: {
					title: '菜单',
				}
			}, {
				path: '/home',
				name: 'Home',
				component: Home,
				meta: {
					title: '首页',
					isTab: true
				}
			},
			{
				path: '/talent',
				name: 'Talent',
				component: Talent,
				meta: {
					title: '人才检索',
					isTab: true
				}
			},
			{
				path: '/blacklist',
				name: 'Blacklist',
				component: Blacklist,
				meta: {
					title: '黑名单',
					isTab: true
				}
			},
			{
				path: '/deletelist',
				name: 'Deletelist',
				component: Deletelist,
				meta: {
					title: '删除库',
					isTab: true
				}
			},
			{
				path: '/label',
				name: 'Label',
				component: Label,
				meta: {
					title: '标签管理',
					isTab: true
				}
			},
			{
				path: '/label_type',
				name: 'Labeltype',
				component: Labeltype,
				meta: {
					title: '标签类别管理',
					isTab: true
				}
			},
			{
				path: '/business',
				name: 'Business',
				component: Business,
				meta: {
					title: '業種管理',
					isTab: true
				}
			},
			{
				path: '/interviewer',
				name: 'Interviewer',
				component: Interviewer,
				meta: {
					title: '面试官管理',
					isTab: true
				}
			},
			{
				path: '/statistics',
				name: 'Statistics',
				component: Statistics,
				meta: {
					title: '数据统计',
					isTab: true
				}
			},
			{
				path: '/user',
				name: 'User',
				component: User,
				meta: {
					title: '用户管理',
					isTab: true
				}
			},
			{
				path: '/log',
				name: 'Log',
				component: Log,
				meta: {
					title: '日志管理',
					isTab: true
				}
			},
			{
				path: '/settings',
				name: 'Settings',
				component: Settings,
				meta: {
					title: '系统设置',
					isTab: true
				}
			},
			// {
			// 	path: "/manuals",
			// 	name: "Manuals",
			// 	// component: () => import('../views/excelSystem/Manual.vue'),	
			// 	component: Manuals,
			// 	meta: {
			// 		title: '系统设置',
			// 		isTab: true
			// 	}
			//   },
			  
			// 表格管理
			{
				path: '/appoint',
				name: 'Appoint',
				component: Appoint,
				meta: {
					title: '予約管理',
					isTab: true
				}
			}, {
				path: '/project',
				name: 'Project',
				component: Project,
				meta: {
					title: 'プロジェクト管理',
					isTab: true
				}
			}, {
				path: '/company',
				name: 'Company',
				component: Company,
				meta: {
					title: '協力会社情報',
					isTab: true
				}
			},{
				path: '/technician',
				name: 'Technician',
				component: Technician,
				meta: {
					title: '技術者管理',
					isTab: true
				}
			}, {
				path: '/customer',
				name: 'Customer',
				component: Customer,
				meta: {
					title: '顧客管理',
					isTab: true
				}
			}, {
				path: '/user_table',
				name: 'UserTable',
				component: UserTable,
				meta: {
					title: '稼働状况',
				}
			}, {
				path: '/project_table',
				name: 'ProjectTable',
				component: ProjectTable,
				meta: {
					title: '売上管理表',
				}
			}, {
				path: '/work_table',
				name: 'WorkTable',
				component: WorkTable,
				meta: {
					title: '作業管理表',
				}
			},
		]
	},
	{
		path: "/404",
		name: "NotFound",
		component: NotFound
	},
	{
		path: '/:pathMatch(.*)*',
		redirect: "/404"
	}
]

const router = createRouter({
	history: createWebHashHistory(),
	routes
})
router.beforeEach((to, from, next) => {
	if (to.name != "Login") {
		let permissions = localStorage.getItem("permissions")
		let token = localStorage.getItem("token")
		if (permissions == null || permissions == "" || token == null || token == "") {
			next({
				name: 'Login'
			})
		}
	}
	return next()
})

export default router