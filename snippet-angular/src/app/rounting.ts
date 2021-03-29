import {Routes} from '@angular/router';

import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component'
import {AppMainComponent} from './app-main/app-main.component';
import { RibbonComponent } from './ribbon/ribbon.component';
import { CreationSnippetComponent } from './creation-snippet/creation-snippet.component';
import { ProfileComponent } from './profile/profile.component';
import { ListAuthorsComponent } from './list-authors/list-authors.component';
import { ListFollowersComponent } from './list-followers/list-followers.component';
import { UploadFileComponent } from './upload-file/upload-file.component';
import { SettingsComponent } from './settings/settings.component';

export const appRoutes: Routes  = [
    {path : "", component : AppMainComponent},
    {path : "login", component: LoginComponent},
    {path : "signup", component: SignupComponent},
    {path : "ribbon", component: RibbonComponent},
    {path : "create/snippet", component: CreationSnippetComponent},
    {path : "profile/:id", component: ProfileComponent},
    {path : "profile", component: ProfileComponent},
    {path : "authors/:id", component: ListAuthorsComponent},
    {path : "followers/:id", component: ListFollowersComponent},
    {path : "upload", component : UploadFileComponent},
    {path : "settings", component : SettingsComponent}
];