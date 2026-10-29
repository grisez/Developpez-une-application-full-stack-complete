import { Routes } from '@angular/router';

import { authGuard, guestGuard } from './core/guards/auth.guards';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    title: 'Accueil - MDD',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/home/home').then((m) => m.Home),
  },
  {
    path: 'login',
    title: 'Connexion - MDD',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    title: 'Inscription - MDD',
    canActivate: [guestGuard],
    loadComponent: () => import('./features/auth/register/register').then((m) => m.Register),
  },
  {
    path: 'feed',
    title: "Fil d'actualité - MDD",
    canActivate: [authGuard],
    loadComponent: () => import('./features/feed/feed').then((m) => m.Feed),
  },
  {
    path: 'articles/new',
    title: 'Créer un article - MDD',
    canActivate: [authGuard],
    loadComponent: () => import('./features/posts/create-post/create-post').then((m) => m.CreatePost),
  },
  {
    // Declared after 'articles/new', so that "new" is never read as an article id
    path: 'articles/:id',
    title: 'Article - MDD',
    canActivate: [authGuard],
    loadComponent: () => import('./features/posts/post-detail/post-detail').then((m) => m.PostDetail),
  },
  {
    path: 'topics',
    title: 'Thèmes - MDD',
    canActivate: [authGuard],
    loadComponent: () => import('./features/topics/topics').then((m) => m.Topics),
  },
  {
    path: 'profile',
    title: 'Profil - MDD',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profile/profile').then((m) => m.Profile),
  },
  {
    path: '**',
    title: 'Page introuvable - MDD',
    loadComponent: () => import('./features/not-found/not-found').then((m) => m.NotFound),
  },
];
