import { Component, Input, OnInit, Output, EventEmitter  } from '@angular/core';
import { RepresentationAuthorDTO } from '../dto/rep.author.dto';
import { AuthService } from '../service/auth.service';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-card-author',
  templateUrl: './card-author.component.html',
  styleUrls: ['./card-author.component.css']
})
export class CardAuthorComponent implements OnInit {

  @Input() representationAuthorDTO : RepresentationAuthorDTO;
  @Output() unsubscribeEvent = new EventEmitter<string>();

  constructor(
    private userService : UserService,
    private authService : AuthService
  ) {
  }

  ngOnInit() {
  }

  isUser() : boolean{
    return this.representationAuthorDTO.id != this.authService.getUserID()
  }

  clickSubscribe() {
    this.userService.subscribeToAuthor(this.representationAuthorDTO.id)
      .subscribe(
        res => {
          console.log(res);
          if (res["status"] == "success") {
            this.representationAuthorDTO.subscription = !this.representationAuthorDTO.subscription
          }
        }, 
        err => {
          console.log(err);
        }
      );
  }

  clickUnsubscribe() {
      this.userService.unsubscribeToAuthor(this.representationAuthorDTO.id)
        .subscribe(
          res => {
            console.log(res);
            if (res["status"] == "success") 
              this.unsubscribeEvent.emit(this.representationAuthorDTO.id);
          },
          err => {
            console.log(err);
          }
        );
  }
}