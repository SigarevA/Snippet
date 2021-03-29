import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RepresentationAuthorDTO } from '../dto/rep.author.dto';
import { AuthService } from '../service/auth.service';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-list-followers',
  templateUrl: './list-followers.component.html',
  styleUrls: ['./list-followers.component.css']
})
export class ListFollowersComponent implements OnInit {

  private userId : string;
  followers : RepresentationAuthorDTO[];

  constructor(    
    private userService : UserService,
    private activateRoute: ActivatedRoute,
    private authService : AuthService
  ) 
  {
    activateRoute.params.subscribe(
      params => {
        this.userId = params["id"];
        console.log(`user id : ${this.userId}`)
        if (this.userId != undefined || this.userId != null) 
          this.loadFollowers();
      }
    )
  }

  handleUnSubscribeEvent(userID : string) {
    console.log(userID);
    console.log("else block");
      let repUser = this.followers.find(
        (value, index, arr) => {
          if (value.id == userID)
            return true;
        }
      );
      console.log()
      if (repUser) 
        repUser.subscription = !repUser.subscription;
  }

  ngOnInit() {
    this.authService.handleUnAuth();
  }

  loadFollowers() {
    this.userService.findFollowersUserByID(this.userId).subscribe(
      res => {
        console.log(res);
        this.followers = res;
      },
      err => {
        console.log(err);
      }
    );
  }
}