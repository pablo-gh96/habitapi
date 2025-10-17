import { Routes } from '@angular/router';
import { ProfilePageComponent } from './profile/profile-page.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { FeedPageComponent } from './feed/feed-page.component';
import { AuthGuard } from './auth/auth.guard';
import { LoginGuard } from './auth/login.guard';
import { NotFoundComponent } from './not-found/not-found.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [LoginGuard] },
  { path: 'register', component: RegisterComponent },
  { path: '', component: FeedPageComponent , canActivate: [AuthGuard]},
  { path: 'not-found', component: NotFoundComponent },
  { path: ':username', component: ProfilePageComponent, canActivate: [AuthGuard] },
  
];
