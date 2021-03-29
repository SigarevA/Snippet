import { Component, OnInit,Input } from '@angular/core';
import { CommentDTO } from '../dto/comment.dto';
import { SnippetDTO } from '../dto/snippet.dto';
import { CommentService } from '../service/comment.service';
import { SnippetService } from '../service/snippet.service';

@Component({
  selector: 'app-card-snippet',
  templateUrl: './card-snippet.component.html',
  styleUrls: ['./card-snippet.component.css']
})
export class CardSnippetComponent implements OnInit {

  @Input() snippet : SnippetDTO;
  collapseComments : boolean = true;
  readonly ID : string = "ID_";
  actualValueLength = 0;
  readonly MAX_LENGTH_COMMENT = 40;
  contentComment : string = "";
  comments : CommentDTO[];

  constructor(
    private snippetService : SnippetService,
    private commentService : CommentService) { }

  ngOnInit() {
  }
  clickLikeBtn() {
    this.snippetService.liskeSnippet(this.snippet.id)
      .subscribe(
        res => {
          console.log(res);
          this.snippet = res;
        },
        err =>{
          console.log(err);
        }
      );
  }

  showComments() {
    this.collapseComments = !this.collapseComments;
    console.log(this.collapseComments);
    if (!this.collapseComments) 
      this.loadComments();
  }

  loadComments() {
    this.commentService.getCommentsByID(this.snippet.id)
    .subscribe(
      res => {
        console.log(res);
        this.comments = res;
        console.log(this.comments);
      },
      err => {
        console.log(err);
      }
  );
  }

  sendComment() {
    if (this.validContentComment())
      this.commentService.sendComment(this.snippet.id, this.contentComment)
        .subscribe(
          res => {
            console.log(res);
            if (res["status"] == "success") {
              this.loadComments();
              this.contentComment = "";
              this.actualValueLength = 0;
            }
          },
          err => {
            console.log(err);
          }
        );
  }

  validContentComment() : boolean {
    return this.actualValueLength != 0;
  }

  keyUpContentComment() {
    if (this.MAX_LENGTH_COMMENT < this.contentComment.trim().length) {
      this.contentComment = this.contentComment.trim().substr(0, this.MAX_LENGTH_COMMENT);
    }
    else 
      this.actualValueLength = this.contentComment.trim().length
  }

  clickLikeSnippet() {
    this.snippetService.liskeSnippet(this.snippet.id)
    .subscribe(
      res => {
        console.log(res);
        this.snippet = res;
      },
      err =>{
        console.log(err);
      }
    );
  }
}