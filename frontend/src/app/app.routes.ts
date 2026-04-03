import {Routes} from '@angular/router';
import {authGuard} from './infrastructure/web/guards/auth.guard';

export const routes: Routes = [
    {
        path: 'login',
        loadComponent: () =>
            import('./infrastructure/web/features/auth/login/login').then(m => m.Login)
    },
    {
        path: 'register',
        loadComponent: () =>
            import('./infrastructure/web/features/account-page/account-page').then(m => m.AccountPage)
    },
    {
        path: '',
        canActivate: [authGuard],
        children: [
            {path: '', redirectTo: 'notifications', pathMatch: 'full'},
            {
                path: 'notifications',
                loadComponent: () =>
                    import('./infrastructure/web/features/notifications/notification-list/notification-list').then(m => m.NotificationList)
            },
            {
                path: 'category-list',
                loadComponent: () =>
                    import('./infrastructure/web/features/categories/category-list/category-list').then(m => m.CategoryList)
            },
            {
                path: 'category-add',
                loadComponent: () =>
                    import('./infrastructure/web/features/categories/category-form/category-form').then(m => m.CategoryForm)
            },
            {
                path: 'category-edit/:id',
                loadComponent: () =>
                    import('./infrastructure/web/features/categories/category-form/category-form').then(m => m.CategoryForm)
            },
            {
                path: 'category-detail/:id',
                loadComponent: () =>
                    import('./infrastructure/web/features/categories/category-detail/category-detail').then(m => m.CategoryDetail)
            },
            {
                path: 'student-list',
                loadComponent: () =>
                    import('./infrastructure/web/features/students/student-list/student-list').then(m => m.StudentList)
            },
            {
                path: 'student-tasks/:studentId',
                loadComponent: () =>
                    import('./infrastructure/web/features/students/student-tasks/student-tasks').then(m => m.StudentTasks)
            },
            {
                path: 'account',
                loadComponent: () =>
                    import('./infrastructure/web/features/account-page/account-page').then(m => m.AccountPage)
            },
            {
                path: 'topic-add',
                loadComponent: () =>
                    import('./infrastructure/web/features/topics/topic-form/topic-form').then(m => m.TopicForm)
            },
            {
                path: 'topic-edit/:id',
                loadComponent: () =>
                    import('./infrastructure/web/features/topics/topic-form/topic-form').then(m => m.TopicForm)
            },
            {
                path: 'topic-detail/:id',
                loadComponent: () =>
                    import('./infrastructure/web/features/topics/topic-detail/topic-detail').then(m => m.TopicDetail)
            },
            {
                path: 'topic-subscribe/:id',
                loadComponent: () =>
                    import('./infrastructure/web/features/topics/subscribe-students/subscribe-students').then(m => m.SubscribeStudents)
            },
            {
                path: 'task-add',
                loadComponent: () =>
                    import('./infrastructure/web/features/tasks/task-form/task-form').then(m => m.TaskForm)
            },
            {
                path: 'task-edit/:id',
                loadComponent: () =>
                    import('./infrastructure/web/features/tasks/task-form/task-form').then(m => m.TaskForm)
            },
            {
                path: 'task-detail/:id',
                loadComponent: () =>
                    import('./infrastructure/web/features/tasks/task-detail/task-detail').then(m => m.TaskDetail)
            },
            {
                path: 'task/:taskId/solution/:id',
                loadComponent: () =>
                    import('./infrastructure/web/features/solutions/solution-detail/solution-detail').then(m => m.SolutionDetail)
            },
            {
                path: 'solution/:id',
                loadComponent: () =>
                    import('./infrastructure/web/features/solutions/solution-detail/solution-detail').then(m => m.SolutionDetail)
            },
            {
                path: 'task/:taskId/solution-form',
                loadComponent: () =>
                    import('./infrastructure/web/features/solutions/solution-form/solution-form').then(m => m.SolutionForm)
            },
        ]
    },
    {path: '**', redirectTo: 'category-list'}
];
