import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthDTO } from '../dto/auth.dto';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly ACCESS_TOKEN : string = "access_token";
  private readonly USER_ID : string = "user_id"

  constructor(
    private http : HttpClient,
    private router: Router 
  ) { }

  public auth(authDTO : AuthDTO) : Observable<any> {
    console.log("auth user");
    const url = `${environment.urlAPI}/auth`;
    return this.http.post(url, authDTO);
  }

  public isAuth() : boolean {
    return localStorage.getItem(this.ACCESS_TOKEN) != null;
  }

  public setAuth(accessToken : string) {
    return localStorage.setItem(this.ACCESS_TOKEN, accessToken);
  }

  public setUserId(userId : string) {
    return localStorage.setItem(this.USER_ID, userId);
  }

  public getToken() : string {
    return localStorage.getItem(this.ACCESS_TOKEN);
  }

  public removeToken(){
    localStorage.removeItem(this.ACCESS_TOKEN);
    localStorage.removeItem(this.USER_ID);
  }

  public getUserID() : string {
    return localStorage.getItem(this.USER_ID);
  }

  public handleUnAuth() {
    if (!this.isAuth()) {
      this.router.navigate(['/login'])
    } 
  }
}