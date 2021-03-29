import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { env } from 'process';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(private http : HttpClient) { }

  public getCommentsByID(snippetID : string) : Observable<any> {
    console.log(`get comments by id : ${snippetID}`);
    const url = `${environment.urlAPI}/api/comments?snippetID=${snippetID}`;

    return this.http.get(url);
  }

  public sendComment(snippetID : string, content : string) : Observable<any> {
    console.log(`send comment with content : ${content}`);
    const url = `${environment.urlAPI}/api/add/comment`;
    return this.http.post(url, {snippetId : snippetID, content : content});
  }

  public likeComment(commentId : string) {
    console.log(`like comment with id : ${commentId}`);
    const url = `${environment.urlAPI}/api/like/comment`;
    return this.http.post(url, {commentId : commentId});
  }

  public getCommentById(commentID : string ) : Observable<any> {
    const url = `${environment.urlAPI}/api/comment?commentID=${commentID}`;
    return this.http.get(url);
  }
}