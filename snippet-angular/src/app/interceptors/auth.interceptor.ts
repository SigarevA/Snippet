import { Injectable } from '@angular/core';
import { HttpHeaders, HttpInterceptor } from '@angular/common/http';
import {  HttpRequest } from '@angular/common/http';
import {  HttpResponse,HttpHandler,HttpErrorResponse,HttpEvent } from '@angular/common/http';
import {tap } from 'rxjs/internal/operators';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';


@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(private authService : AuthService, private router: Router) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    let authReq = req.clone();
    const baseUrl = authReq.url;
    if (baseUrl.includes(`${environment.urlAPI}/api`))
    {
        console.log("ds");
        authReq = req.clone( {
          headers : req.headers.set("Authorization", this.authService.getToken())
        });
    }

    return next.handle(authReq).pipe(
      tap(
        event => {
          if (event instanceof HttpResponse) 
          { 
            console.log(event.headers.keys());
            console.log('Server response')
          }
        },
        err => {
          if (err instanceof HttpErrorResponse) {
            if (err.status == 401) console.log('Unauthorized')
            let headers = err.headers;
            let error = headers.get("Error")
            console.log(error);
            if (error != null && error == "token don't valid") {
              this.authService.removeToken();
              this.router.navigate([''])
            }
          }
        }
      )
    )
  }
}