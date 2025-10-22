import { Routes } from '@angular/router';
import { ProfilePageComponent } from './features/profile/profile-page.component';
import { LoginComponent } from './features/login/login.component';
import { RegisterComponent } from './features/register/register.component';
import { FeedPageComponent } from './features/home/feed-page.component';
import { AuthGuard } from './auth/auth.guard';
import { LoginGuard } from './auth/login.guard';
import { NotFoundComponent } from './features/not-found/not-found.component';
import { GenericLayoutComponent } from './layout/generic-layout/generic-layout.component';

export const routes: Routes = [
  // Rutas públicas
  { path: 'login', component: LoginComponent, canActivate: [LoginGuard] },
  { path: 'register', component: RegisterComponent },

  // Rutas protegidas dentro del layout
  {
    path: '',
    component: GenericLayoutComponent,       // ⬅️ layout contenedor
    canActivate: [AuthGuard],
    children: [
      { path: 'home', component: FeedPageComponent },
      { path: 'profile', component: ProfilePageComponent },
      { path: 'not-found', component: NotFoundComponent },
      { path: '', redirectTo: 'home', pathMatch: 'full' }
    ]
  },

  // Cualquier otra ruta
  { path: '**', redirectTo: 'not-found' }
];
