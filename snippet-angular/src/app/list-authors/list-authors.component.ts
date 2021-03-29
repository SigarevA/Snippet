import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RepresentationAuthorDTO } from '../dto/rep.author.dto';
import { AuthService } from '../service/auth.service';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-list-authors',
  templateUrl: './list-authors.component.html',
  styleUrls: ['./list-authors.component.css']
})
export class ListAuthorsComponent implements OnInit {

  private userId : string;
  authors : RepresentationAuthorDTO[];
  
  constructor( 
    private userService : UserService,
    private authService : AuthService,
    private activateRoute: ActivatedRoute){
      activateRoute.params.subscribe(
        params => {
          this.userId = params["id"];
          console.log(`user id : ${this.userId}`)
          if (this.userId != undefined || this.userId != null) 
            this.loadAuthors();
        }
      )
  }

  handleUnSubscribeEvent(userID : string) {
    if (this.authService.getUserID() == this.userId) {
      console.log("if block");
      this.authors = this.authors.filter(
        (value, index, arr) => {
          if (value.id != userID)
            return value;
        }
      )
    }
    else {
      console.log("else block");
      let repUser = this.authors.find(
        (value, index, arr) => {
          if (value.id == userID)
            return true;
        }
      );
      console.log()
      if (repUser) 
        repUser.subscription = true;
    }
    console.log(userID);
  }

  ngOnInit() {
    this.authService.handleUnAuth();
  }

  loadAuthors() {
    this.userService.findAuthorsUserById(this.userId).subscribe(
      res => {
        console.log(res);
        this.authors = res;
      },
      err => {
        console.log(err);
      }
    );
  } 
}