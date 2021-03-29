import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { RepresentationAuthorDTO } from '../dto/rep.author.dto';
import { UserDTO } from '../dto/user.dto';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient : HttpClient) { }

  public getUserInfo() : Observable<UserDTO> {
    const url = `${environment.urlAPI}/api/user/info`;
    return this.httpClient.get<UserDTO>(url);
  }

  public findUserInfoById (userId : string ) : Observable<UserDTO> {
    const url = `${environment.urlAPI}/api/user/info/${userId}`;
    return this.httpClient.get<UserDTO>(url);
  }

  public findAuthorsUserById(userID : string ) : Observable<RepresentationAuthorDTO[]> {
    const url = `${environment.urlAPI}/api/authors/${userID}`;
    return this.httpClient.get<RepresentationAuthorDTO[]>(url)
  }

  public findFollowersUserByID(userId : string ) : Observable<RepresentationAuthorDTO[]> {
    const url = `${environment.urlAPI}/api/followers/${userId}`;
    return this.httpClient.get<RepresentationAuthorDTO[]>(url)
  }

  public subscribeToAuthor(id : string) : Observable<any>{
    const url = `${environment.urlAPI}/api/subscribe`;
    return this.httpClient.post(url, {id : id});
  }

  public unsubscribeToAuthor(id : string ) : Observable<any> {
    const url = `${environment.urlAPI}/api/unsubscribe`;
    return this.httpClient.post(url, {id : id});
  }

  public saveUserImage(image : string) : Observable<any> {
    const url = `${environment.urlAPI}/api/user/image`;
    let fr = new FormData();
    fr.append("image", image);
    return this.httpClient.post(url, fr);
  }
}