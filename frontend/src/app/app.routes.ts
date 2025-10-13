import { Routes } from '@angular/router';
import { ProfilePageComponent } from './profile/profile-page.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';

export const routes: Routes = [
  { path: 'me', component:  ProfilePageComponent},
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
];
