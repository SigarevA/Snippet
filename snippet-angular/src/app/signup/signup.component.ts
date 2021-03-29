import { Component, OnInit } from '@angular/core';
import { RegistrationService } from '../service/registration.service';
import { RegistrationDTO } from '../dto/registration.dto';
import { dashCaseToCamelCase } from '@angular/compiler/src/util';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  registratinDTO : RegistrationDTO = {name : "", email : "", password : ""};
  validName = true;
  validEmail = true;
  validPassword = true;
  msgNullField = "Поле не может быть пустым!";
  private emailRegex = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;
  msgErrorEmail = "";
  emailIsBusy = false;

  constructor(
    private registrationService : RegistrationService,
    private authService : AuthService,
    private router: Router
    ) { }

  ngOnInit() {
  }

  changeName() {
    console.log("dsa");
  }

  calculateValid() {
    this.validName = !(this.registratinDTO.name.trim().length == 0);
    this.validEmail = !(this.registratinDTO.email.trim().length == 0);
    this.validPassword = !(this.registratinDTO.password.trim().length == 0);
    if (!this.validEmail)
      this.msgErrorEmail = this.msgNullField;
    else {
      let d = this.registratinDTO.email.search(this.emailRegex);
      this.validEmail = d == 0;
      if (!this.validEmail)
        this.msgErrorEmail = "Неверная форма почты!";
    }
  }

  checkValid() : boolean {
    console.log("name " + this.validName + " email : " + this.validEmail);
    return this.validName && this.validPassword && this.validEmail;
  } 

  click () : any {
    this.calculateValid();
    if (this.checkValid()) {
    console.log(this.registratinDTO);
    this.registrationService.registry(this.registratinDTO)
      .subscribe(
        res => {
          console.log(res);
          if ( res["status"] == "success" ) {
            this.authService.auth(
              {
                email : this.registratinDTO.email.toString(),
                 password : this.registratinDTO.password.toString()
              }
            )
              .subscribe(
                res => {
                  const status : string = res["status"];
                  if (status == "failure") {
                  }
                  if (status == "success") {
                    const accessToken : string = res["token"];
                    this.authService.setAuth(accessToken);
                    const userId : string = res["userId"];
                    this.authService.setUserId(userId);
                    this.router.navigate(['ribbon']);
                  }
                },
                err => {}
              )
          }
          if ( res["status"] == "failure" ) {
            if (res["message"] == "mail is busy") {
              this.emailIsBusy = true;
            }
          }
        },
        err => {
          console.log(err);
        }
      );
    }
  }
}