import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { ResizedEvent } from 'angular-resize-event';
import { Router } from '@angular/router';

@Component({
  selector: 'app-app-header',
  templateUrl: './app-header.component.html',
  styleUrls: ['./app-header.component.css']
})
export class AppHeaderComponent implements OnInit {
  
  private width : number;
  private height : number;
  logoLinkPage : string = "/";

  latentMenu : boolean;

  constructor(
    private authService : AuthService,
    private router: Router) { }
  
  ngOnInit() {
    this.initSize();
    if (this.authService.isAuth()) {
      this.logoLinkPage = "ribbon"
    }
  }

  checkAuth() {
    return this.authService.isAuth();
  }

  initSize() : void {
    console.log("check width client");
    this.latentMenu = document.documentElement.clientWidth < 768;
    this.width = document.documentElement.clientWidth;
    this.height = document.documentElement.clientHeight;
  }

  clickExitUser(event : MouseEvent) {
    console.log(document.documentElement.clientWidth);
    event.preventDefault();
    console.log(event);
    this.authService.removeToken();
    this.router.navigate(['']);
  }

  onResized(event: ResizedEvent) {
    this.initSize();
  }
  
  checkMenuNewLine() : boolean {
    return this.width < 210;
  }

  getUserId() : string {
    return this.authService.getUserID();
  }
}