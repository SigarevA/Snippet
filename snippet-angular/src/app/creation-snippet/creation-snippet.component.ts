import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { SnippetService } from '../service/snippet.service';

@Component({
  selector: 'app-creation-snippet',
  templateUrl: './creation-snippet.component.html',
  styleUrls: ['./creation-snippet.component.css']
})
export class CreationSnippetComponent implements OnInit {

  content : string = "";
  length : number = 0;
  maxLength = 150;
  nullContentError = false;
  creationSuccess = false;
  creationFailure = false;

  constructor(
    private snippetService : SnippetService,
    private authService : AuthService
  ) 
  { }

  ngOnInit() {
    this.authService.handleUnAuth();
  }

  validContent() : boolean {
    if (this.content.trim().length == 0)  
      this.nullContentError = true;
    else 
      this.nullContentError = false;
    return this.nullContentError;
  }

  clickPost() {
    this.creationSuccess = false;
    this.creationFailure = false;
    if (!this.validContent())
      this.snippetService.createSnippet(this.content).subscribe(
        res => {
          console.log(res);
          if (res["status"] == "success") {
            this.creationSuccess = true;
          }
          if (res["status"] == "failure") {
            this.creationFailure = true;
          }
        }, 
        err => {
          console.log(err);
          this.creationFailure = true;
        }
      );
  }

  fillContent(event : KeyboardEvent ) {
    console.log(this.content);
    console.log(this.content.length)
    if (this.content.length > this.maxLength) {
      this.content = this.content.substr(0, this.maxLength);
      this.length = this.maxLength;
    }
    else {
      this.length = this.content.length;
    }
  }
}