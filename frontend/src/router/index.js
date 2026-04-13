import { createRouter, createWebHistory } from 'vue-router'
import { useTokenStore } from '@/stores/token'
import { jwtDecode } from 'jwt-decode'
import { useUserInfoStore } from '@/stores/userInfo'

// Auth pages
import Login from '@/pages/auth/Login.vue'
import Register from '@/pages/auth/Register.vue'
import ForgotPassword from '@/pages/auth/ForgotPassword.vue'
import ResetPassword from '@/pages/auth/ResetPassword.vue'

// Dashboard shell
import Dashboard from '@/components/Dashboard.vue'

// Common pages
import Home from '@/pages/Home.vue'
import Profile from '@/pages/Profile.vue'
import Forbidden from '@/pages/Forbidden.vue'

// Admin pages
import Sections from '@/pages/admin/Sections.vue'
import SectionDetail from '@/pages/admin/SectionDetail.vue'
import SectionCreate from '@/pages/admin/SectionCreate.vue'
import SectionEdit from '@/pages/admin/SectionEdit.vue'
import ActiveWeeks from '@/pages/admin/ActiveWeeks.vue'
import Teams from '@/pages/admin/Teams.vue'
import TeamDetail from '@/pages/admin/TeamDetail.vue'
import TeamCreate from '@/pages/admin/TeamCreate.vue'
import TeamEdit from '@/pages/admin/TeamEdit.vue'
import Students from '@/pages/admin/Students.vue'
import StudentDetail from '@/pages/admin/StudentDetail.vue'
import Instructors from '@/pages/admin/Instructors.vue'
import InstructorDetail from '@/pages/admin/InstructorDetail.vue'
import InviteStudents from '@/pages/admin/InviteStudents.vue'
import InviteInstructors from '@/pages/admin/InviteInstructors.vue'
import AssignStudents from '@/pages/admin/AssignStudents.vue'
import AssignInstructors from '@/pages/admin/AssignInstructors.vue'
import Rubrics from '@/pages/admin/Rubrics.vue'
import RubricCreate from '@/pages/admin/RubricCreate.vue'

// Student pages
import MyActivities from '@/pages/student/MyActivities.vue'
import MyEvaluations from '@/pages/student/MyEvaluations.vue'
import SubmitEvaluation from '@/pages/student/SubmitEvaluation.vue'
import MyEvalReport from '@/pages/student/MyEvalReport.vue'

// Instructor pages
import SectionActivities from '@/pages/instructor/SectionActivities.vue'
import SectionEvalReport from '@/pages/instructor/SectionEvalReport.vue'
import StudentEvalReport from '@/pages/instructor/StudentEvalReport.vue'
import TeamActivities from '@/pages/instructor/TeamActivities.vue'
import StudentActivities from '@/pages/instructor/StudentActivities.vue'

const routes = [
  { path: '/login', name: 'Login', component: Login, meta: { visitorOnly: true } },
  { path: '/register', name: 'Register', component: Register, meta: { visitorOnly: true } },
  { path: '/forgot-password', name: 'ForgotPassword', component: ForgotPassword, meta: { visitorOnly: true } },
  { path: '/reset-password', name: 'ResetPassword', component: ResetPassword, meta: { visitorOnly: true } },
  { path: '/403', name: 'Forbidden', component: Forbidden },
  {
    path: '/',
    component: Dashboard,
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Home', component: Home, meta: { title: 'Home', icon: 'HomeFilled', isMenuItem: true } },
      { path: 'profile', name: 'Profile', component: Profile, meta: { title: 'Profile' } },

      // Admin routes
      { path: 'sections', name: 'Sections', component: Sections, meta: { title: 'Sections', icon: 'Collection', isMenuItem: true, requiresPermissions: ['admin'] } },
      { path: 'sections/create', name: 'SectionCreate', component: SectionCreate, meta: { title: 'Create Section', requiresPermissions: ['admin'] } },
      { path: 'sections/:id', name: 'SectionDetail', component: SectionDetail, meta: { title: 'Section Detail', requiresPermissions: ['admin'] } },
      { path: 'sections/:id/edit', name: 'SectionEdit', component: SectionEdit, meta: { title: 'Edit Section', requiresPermissions: ['admin'] } },
      { path: 'sections/:id/active-weeks', name: 'ActiveWeeks', component: ActiveWeeks, meta: { title: 'Active Weeks', requiresPermissions: ['admin'] } },
      { path: 'sections/:id/invite-students', name: 'InviteStudents', component: InviteStudents, meta: { title: 'Invite Students', requiresPermissions: ['admin'] } },
      { path: 'sections/:id/assign-students', name: 'AssignStudents', component: AssignStudents, meta: { title: 'Assign Students', requiresPermissions: ['admin'] } },
      { path: 'sections/:id/assign-instructors', name: 'AssignInstructors', component: AssignInstructors, meta: { title: 'Assign Instructors', requiresPermissions: ['admin'] } },

      { path: 'teams', name: 'Teams', component: Teams, meta: { title: 'Teams', icon: 'UserFilled', isMenuItem: true, requiresPermissions: ['admin', 'instructor'] } },
      { path: 'teams/create', name: 'TeamCreate', component: TeamCreate, meta: { title: 'Create Team', requiresPermissions: ['admin'] } },
      { path: 'teams/:id', name: 'TeamDetail', component: TeamDetail, meta: { title: 'Team Detail', requiresPermissions: ['admin', 'instructor'] } },
      { path: 'teams/:id/edit', name: 'TeamEdit', component: TeamEdit, meta: { title: 'Edit Team', requiresPermissions: ['admin'] } },

      { path: 'students', name: 'Students', component: Students, meta: { title: 'Students', icon: 'User', isMenuItem: true, requiresPermissions: ['admin', 'instructor'] } },
      { path: 'students/:id', name: 'StudentDetail', component: StudentDetail, meta: { title: 'Student Detail', requiresPermissions: ['admin', 'instructor'] } },

      { path: 'instructors', name: 'Instructors', component: Instructors, meta: { title: 'Instructors', icon: 'Avatar', isMenuItem: true, requiresPermissions: ['admin'] } },
      { path: 'instructors/:id', name: 'InstructorDetail', component: InstructorDetail, meta: { title: 'Instructor Detail', requiresPermissions: ['admin'] } },
      { path: 'invite-instructors', name: 'InviteInstructors', component: InviteInstructors, meta: { title: 'Invite Instructors', requiresPermissions: ['admin'] } },

      { path: 'rubrics', name: 'Rubrics', component: Rubrics, meta: { title: 'Rubrics', icon: 'Document', isMenuItem: true, requiresPermissions: ['admin'] } },
      { path: 'rubrics/create', name: 'RubricCreate', component: RubricCreate, meta: { title: 'Create Rubric', requiresPermissions: ['admin'] } },

      // Student routes
      { path: 'my-activities', name: 'MyActivities', component: MyActivities, meta: { title: 'My Activities', icon: 'Edit', isMenuItem: true, requiresPermissions: ['student'] } },
      { path: 'my-evaluations', name: 'MyEvaluations', component: MyEvaluations, meta: { title: 'Peer Evaluations', icon: 'Star', isMenuItem: true, requiresPermissions: ['student'] } },
      { path: 'submit-evaluation', name: 'SubmitEvaluation', component: SubmitEvaluation, meta: { title: 'Submit Evaluation', requiresPermissions: ['student'] } },
      { path: 'my-eval-report', name: 'MyEvalReport', component: MyEvalReport, meta: { title: 'My Eval Report', icon: 'DataAnalysis', isMenuItem: true, requiresPermissions: ['student'] } },

      // Instructor routes
      { path: 'section-activities', name: 'SectionActivities', component: SectionActivities, meta: { title: 'Section Activities', icon: 'Notebook', isMenuItem: true, requiresPermissions: ['admin', 'instructor'] } },
      { path: 'team-activities/:teamId', name: 'TeamActivities', component: TeamActivities, meta: { title: 'Team Activities', requiresPermissions: ['admin', 'instructor'] } },
      { path: 'student-activities/:studentId', name: 'StudentActivities', component: StudentActivities, meta: { title: 'Student Activities', requiresPermissions: ['admin', 'instructor'] } },
      { path: 'section-eval-report', name: 'SectionEvalReport', component: SectionEvalReport, meta: { title: 'Section Eval Report', icon: 'TrendCharts', isMenuItem: true, requiresPermissions: ['admin', 'instructor'] } },
      { path: 'student-eval-report/:studentId', name: 'StudentEvalReport', component: StudentEvalReport, meta: { title: 'Student Eval Report', requiresPermissions: ['admin', 'instructor'] } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const tokenStore = useTokenStore()
  const userInfoStore = useUserInfoStore()
  const token = tokenStore.token

  let isAuthenticated = false
  if (token) {
    try {
      const decoded = jwtDecode(token)
      isAuthenticated = decoded.exp * 1000 > Date.now()
    } catch {
      isAuthenticated = false
    }
  }

  if (to.meta.requiresAuth && !isAuthenticated) {
    return next('/login')
  }

  if (to.meta.visitorOnly && isAuthenticated) {
    return next(from.path || '/')
  }

  if (to.meta.requiresPermissions && isAuthenticated) {
    const userRoles = userInfoStore.roles
    const requiredPerms = to.meta.requiresPermissions
    // Admin has access to everything
    if (userRoles.includes('admin')) return next()
    // Check if user has at least one required permission
    const hasPermission = requiredPerms.some(p => userRoles.includes(p))
    if (!hasPermission) return next('/403')
  }

  next()
})

export default router
