import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RegistrationDTO } from '../dto/registration.dto';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  constructor(private http : HttpClient) { }

  public registry ( registrationDTO : RegistrationDTO ) : Observable<any> {
    const url = `${environment.urlAPI}/registration`;
    return this.http.post(url, registrationDTO);
  }
}