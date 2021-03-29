import { Component, Input, OnInit } from '@angular/core';
import { map } from 'rxjs/internal/operators';
import { CommentDTO } from '../dto/comment.dto';
import { CommentService } from '../service/comment.service';

@Component({
  selector: 'app-card-comment',
  templateUrl: './card-comment.component.html',
  styleUrls: ['./card-comment.component.css']
})
export class CardCommentComponent implements OnInit {

  @Input() comment : CommentDTO; 

  constructor(
    private commentService : CommentService
    ) {
   }

  ngOnInit() {
  }

  loadInfoComment() {
    this.commentService.getCommentById(this.comment.id)
      .subscribe(
        res => {
          console.log(res);
          this.comment = res;
        },
        err => {
          console.log(err);
        }
      )
  }

  clickLikeBtn() {
    this.commentService.likeComment(this.comment.id)
      .subscribe(
        res => {
          console.log(res);
          if (res["status"] == "success") 
            this.loadInfoComment();
        },
        err => {
          console.log(err);
        }
      )
  }
}