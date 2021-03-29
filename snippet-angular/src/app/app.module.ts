import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule }   from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AppMainComponent } from './app-main/app-main.component';
import { AppHeaderComponent } from './app-header/app-header.component';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import {appRoutes} from './rounting';
import {RouterModule} from '@angular/router';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { RibbonComponent } from './ribbon/ribbon.component';
import { AngularResizedEventModule } from 'angular-resize-event';
import { CardSnippetComponent } from './card-snippet/card-snippet.component';
import { CreationSnippetComponent } from './creation-snippet/creation-snippet.component';
import { ProfileComponent } from './profile/profile.component';
import { ListAuthorsComponent } from './list-authors/list-authors.component';
import { CardAuthorComponent } from './card-author/card-author.component';
import { ListFollowersComponent } from './list-followers/list-followers.component';
import { CardCommentComponent } from './card-comment/card-comment.component';
import { NgxFileDropModule } from 'ngx-file-drop';
import { UploadFileComponent } from './upload-file/upload-file.component';
import { ImageCropperModule } from 'ngx-image-cropper';
import { SettingsComponent } from './settings/settings.component';

@NgModule({
  declarations: [
    AppComponent,
    AppMainComponent,
    AppHeaderComponent,
    LoginComponent,
    SignupComponent,
    RibbonComponent,
    CardSnippetComponent,
    CreationSnippetComponent,
    ProfileComponent,
    ListAuthorsComponent,
    CardAuthorComponent,
    ListFollowersComponent,
    CardCommentComponent,
    UploadFileComponent,
    SettingsComponent, 
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RouterModule.forRoot(appRoutes),
    HttpClientModule,
    FormsModule,
    AngularResizedEventModule,
    NgxFileDropModule,
    ImageCropperModule
  ],
  providers: [    
    {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }