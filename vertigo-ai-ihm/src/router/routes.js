
const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '', component: () => import('pages/Overview.vue') },
      { path: 'explore/:bundle', component: () => import('pages/Bundle.vue') },
      { path: 'create/', component: () => import('pages/Create.vue') },
      { path: 'deploy/', component: () => import('pages/Deploy.vue') },
      { path: 'fork/:bundle/:version', component: () => import('pages/Create.vue') },
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '*',
    component: () => import('pages/Error404.vue')
  }
]

export default routes
