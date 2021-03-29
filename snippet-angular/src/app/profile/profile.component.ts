import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SnippetDTO } from '../dto/snippet.dto';
import { UserDTO } from '../dto/user.dto';
import { AuthService } from '../service/auth.service';
import { SnippetService } from '../service/snippet.service';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user : UserDTO = null;
  listSnippets : SnippetDTO[];
  private id : string;
  anyProfile : boolean = false;
  subscription : boolean = false;

  constructor(
    private router : Router,
    private activateRoute: ActivatedRoute,
    private userService : UserService,
    private snippetService : SnippetService,
    private authService : AuthService
    ) {
      activateRoute.params.subscribe(
        params=> { 
          this.id = params['id'];
          if (this.id == undefined) {
            this.loadUserInfoByAuth();
          }
          else {
            console.log(this.id); 
            this.loadUserInfoById(this.id);
          }
        }
      );
   }

   loadUserInfoByAuth() {
    this.userService.getUserInfo().subscribe(
      res => {
        console.log(res);
        this.user = res;
        this.loadSnippets(this.user.id);
      },
      err => {
        console.log(err);
      }
    );
   }

   loadUserInfoById(id : string) {
    this.userService.findUserInfoById(id).subscribe(
      res => {
        console.log(res);
        this.user = res;
        this.loadSnippets(this.user.id);
        if ( this.user.id != this.authService.getUserID()) 
          this.anyProfile = true
        this.checkUserSubscribeToAuthor();
      },
      err => {
        console.log(err);
      }
    );
   }

  ngOnInit() {
    this.authService.handleUnAuth();
  }

  loadSnippets(id : string) {
    this.snippetService.getUserSnippetsByID(id).subscribe(
      res => {
        console.log(res);
        this.listSnippets = res;
      },
      err => {
        console.log(err);
      }
    )
  } 

  clickFollowers() {
    console.log(this.user);
    this.router.navigate(["followers", this.user.id]);
  }

  clickAuhtors () {
    this.router.navigate(["authors", this.user.id]);
  }

  checkUserSubscribeToAuthor() {
    const userId = this.authService.getUserID();
    if (this.user.followers) 
    {
      this.subscription = !this.user.followers.includes(userId);
    }
    else {
      this.subscription = true;
    }
  }

  clickSubscribe() {
    this.userService.subscribeToAuthor(this.user.id).subscribe(
      res => {
        console.log(res);
        if (res["status"] == "success") {
          this.loadUserInfoById(this.user.id);
        }
      },
      err => {
        console.log(err);
      }
    )
  }

  clickUnSubscribe() {
    this.userService.unsubscribeToAuthor(this.user.id).subscribe(
      res => {
        console.log(res);
        if (res["status"] == "success") {
          this.loadUserInfoById(this.user.id);
        }
      },
      err => {
        console.log(err);
      }
    )
  }
}