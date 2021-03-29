import { Component, OnInit } from '@angular/core';
import { AuthDTO } from '../dto/auth.dto';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  authError : boolean = false;
  validPassword = true;
  validEmail = true;
  authDTO : AuthDTO = {email : "", password : ""};
  msgNullField = "Поле не может быть пустым!";

  constructor(private authService: AuthService,
    private router: Router) { }

  ngOnInit() {
  }

  calculateValid() {
    this.validEmail = !(this.authDTO.email.trim().length == 0);
    this.validPassword = !(this.authDTO.password.trim().length == 0);
  }

  get checkValid() : boolean {
    return this.validPassword && this.validEmail;
  } 


  clickonAuthorizationBtn() {
    console.log("authorization event handling");
    this.calculateValid();
    if (this.checkValid)
      this.authService.auth(this.authDTO).subscribe(
        res => {
          console.log(res);
          const status : string = res["status"];
          if (status == "failure") {
            this.authError = true;
          }
          if (status == "success") {
            const accessToken : string = res["token"];
            this.authService.setAuth(accessToken);
            const userId : string = res["userId"];
            this.authService.setUserId(userId);
            this.router.navigate(['ribbon']);
          }
        },
        err => {
          console.log(err);
        }
      );
  } 
}